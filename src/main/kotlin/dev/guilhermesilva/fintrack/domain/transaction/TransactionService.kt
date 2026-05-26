package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.application.common.isNotInFarFuture
import dev.guilhermesilva.fintrack.application.common.normalizedText
import dev.guilhermesilva.fintrack.domain.category.Category
import dev.guilhermesilva.fintrack.domain.category.CategoryRepository
import dev.guilhermesilva.fintrack.domain.transaction.Transaction
import dev.guilhermesilva.fintrack.domain.transaction.TransactionRepository
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.infra.exception.BusinessException
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    @Transactional
    fun create(
        principal: UserPrincipal,
        request: CreateTransactionRequest
    ): TransactionResponse {
        val amount = requireNotNull(request.amount)
        val type = requireNotNull(request.type)
        val transactionDate = requireNotNull(request.transactionDate)
        val categoryId = requireNotNull(request.categoryId)

        validateTransactionDate(transactionDate)

        val category = findCategoryOrThrow(
            principal = principal,
            categoryId = categoryId,
            type = type
        )

        val transaction = Transaction(
            amount = amount,
            description = request.description.normalizedText(),
            type = type,
            transactionDate = transactionDate,
            category = category,
            user = principal.user
        )

        return transactionRepository.save(transaction).toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(
        principal: UserPrincipal,
        type: TransactionType?,
        categoryId: UUID?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<TransactionResponse> {
        validateDateRange(startDate, endDate)

        val specification =
            TransactionSpecifications.byUserId(principal.id)
                .and(TransactionSpecifications.byType(type))
                .and(TransactionSpecifications.byCategoryId(categoryId))
                .and(TransactionSpecifications.byStartDate(startDate))
                .and(TransactionSpecifications.byEndDate(endDate))

        return transactionRepository.findAll(specification, pageable)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findById(
        principal: UserPrincipal,
        id: UUID
    ): TransactionResponse =
        findTransactionOrThrow(principal, id).toResponse()

    @Transactional
    fun update(
        principal: UserPrincipal,
        id: UUID,
        request: UpdateTransactionRequest
    ): TransactionResponse {
        val currentTransaction = findTransactionOrThrow(principal, id)

        val updatedType = request.type ?: currentTransaction.type
        val updatedDate = request.transactionDate ?: currentTransaction.transactionDate

        validateTransactionDate(updatedDate)

        val updatedCategory = request.categoryId
            ?.let { categoryId ->
                findCategoryOrThrow(
                    principal = principal,
                    categoryId = categoryId,
                    type = updatedType
                )
            }
            ?: currentTransaction.category.also {
                validateCategoryType(it, updatedType)
            }

        val updatedTransaction = currentTransaction.copy(
            amount = request.amount ?: currentTransaction.amount,
            description = request.description
                ?.normalizedText()
                ?: currentTransaction.description,
            type = updatedType,
            transactionDate = updatedDate,
            category = updatedCategory
        )

        return transactionRepository.save(updatedTransaction).toResponse()
    }

    @Transactional
    fun delete(
        principal: UserPrincipal,
        id: UUID
    ) {
        val transaction = findTransactionOrThrow(principal, id)
        transactionRepository.delete(transaction)
    }

    private fun findTransactionOrThrow(
        principal: UserPrincipal,
        id: UUID
    ): Transaction =
        transactionRepository.findByIdAndUser(id, principal.user)
            ?: throw BusinessException.NotFound("Transaction not found")

    private fun findCategoryOrThrow(
        principal: UserPrincipal,
        categoryId: UUID,
        type: TransactionType
    ): Category {
        val category = categoryRepository.findByIdAndUserAndActiveTrue(
            id = categoryId,
            user = principal.user
        ) ?: throw BusinessException.NotFound("Category not found")

        validateCategoryType(category, type)

        return category
    }

    private fun validateCategoryType(
        category: Category,
        transactionType: TransactionType
    ) {
        if (category.type != transactionType) {
            throw BusinessException.BadRequest("Category type must match transaction type")
        }
    }

    private fun validateTransactionDate(transactionDate: LocalDate) {
        if (!transactionDate.isNotInFarFuture()) {
            throw BusinessException.BadRequest(
                "Transaction date cannot be more than 1 year in the future"
            )
        }
    }

    private fun validateDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?
    ) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw BusinessException.BadRequest("Start date cannot be after end date")
        }
    }
}