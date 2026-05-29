package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.domain.category.Category
import dev.guilhermesilva.fintrack.domain.category.CategoryRepository
import dev.guilhermesilva.fintrack.domain.transaction.Transaction
import dev.guilhermesilva.fintrack.domain.transaction.TransactionRepository
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.domain.user.User
import dev.guilhermesilva.fintrack.infra.exception.BusinessException
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class TransactionServiceTest {

    private val transactionRepository: TransactionRepository = mock()
    private val categoryRepository: CategoryRepository = mock()

    private val transactionService = TransactionService(
        transactionRepository = transactionRepository,
        categoryRepository = categoryRepository
    )

    private val user = User(
        id = UUID.randomUUID(),
        name = "Guilherme Silva",
        email = "gui@gmail.com",
        passwordHash = "hashed-password"
    )

    private val principal = UserPrincipal(user)

    @Test
    fun `should create transaction successfully`() {
        val categoryId = UUID.randomUUID()

        val category = Category(
            id = categoryId,
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            user = user
        )

        val request = CreateTransactionRequest(
            amount = BigDecimal("59.90"),
            description = " Mercado ",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now(),
            categoryId = categoryId
        )

        val savedTransaction = Transaction(
            id = UUID.randomUUID(),
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now(),
            category = category,
            user = user
        )

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = categoryId,
                user = user
            )
        ).thenReturn(category)

        whenever(transactionRepository.save(any()))
            .thenReturn(savedTransaction)

        val response = transactionService.create(principal, request)

        assertEquals(savedTransaction.id, response.id)
        assertEquals(BigDecimal("59.90"), response.amount)
        assertEquals("Mercado", response.description)
        assertEquals(TransactionType.EXPENSE, response.type)
        assertEquals(categoryId, response.category.id)
    }

    @Test
    fun `should throw not found when category does not exist`() {
        val categoryId = UUID.randomUUID()

        val request = CreateTransactionRequest(
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now(),
            categoryId = categoryId
        )

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = categoryId,
                user = user
            )
        ).thenReturn(null)

        assertThrows(BusinessException.NotFound::class.java) {
            transactionService.create(principal, request)
        }
    }

    @Test
    fun `should throw bad request when category type does not match transaction type`() {
        val categoryId = UUID.randomUUID()

        val category = Category(
            id = categoryId,
            name = "Salário",
            type = TransactionType.INCOME,
            user = user
        )

        val request = CreateTransactionRequest(
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now(),
            categoryId = categoryId
        )

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = categoryId,
                user = user
            )
        ).thenReturn(category)

        assertThrows(BusinessException.BadRequest::class.java) {
            transactionService.create(principal, request)
        }
    }

    @Test
    fun `should find transaction by id`() {
        val transactionId = UUID.randomUUID()

        val category = Category(
            id = UUID.randomUUID(),
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            user = user
        )

        val transaction = Transaction(
            id = transactionId,
            amount = BigDecimal("100.00"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now(),
            category = category,
            user = user
        )

        whenever(transactionRepository.findByIdAndUser(transactionId, user))
            .thenReturn(transaction)

        val response = transactionService.findById(principal, transactionId)

        assertEquals(transactionId, response.id)
        assertEquals("Mercado", response.description)
        assertEquals(BigDecimal("100.00"), response.amount)
    }

    @Test
    fun `should throw not found when transaction does not exist`() {
        val transactionId = UUID.randomUUID()

        whenever(transactionRepository.findByIdAndUser(transactionId, user))
            .thenReturn(null)

        assertThrows(BusinessException.NotFound::class.java) {
            transactionService.findById(principal, transactionId)
        }
    }

    @Test
    fun `should delete transaction successfully`() {
        val transactionId = UUID.randomUUID()

        val category = Category(
            id = UUID.randomUUID(),
            name = "Transporte",
            type = TransactionType.EXPENSE,
            user = user
        )

        val transaction = Transaction(
            id = transactionId,
            amount = BigDecimal("20.00"),
            description = "Uber",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now(),
            category = category,
            user = user
        )

        whenever(transactionRepository.findByIdAndUser(transactionId, user))
            .thenReturn(transaction)

        transactionService.delete(principal, transactionId)
    }

    @Test
    fun `should throw bad request when start date is after end date`() {
        assertThrows(BusinessException.BadRequest::class.java) {
            transactionService.findAll(
                principal = principal,
                type = null,
                categoryId = null,
                startDate = LocalDate.of(2026, 5, 31),
                endDate = LocalDate.of(2026, 5, 1),
                pageable = org.springframework.data.domain.PageRequest.of(0, 10)
            )
        }
    }
    @Test
    fun `should update transaction successfully`() {
        val transactionId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()

        val category = Category(
            id = categoryId,
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            user = user
        )

        val currentTransaction = Transaction(
            id = transactionId,
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.of(2026, 5, 26),
            category = category,
            user = user
        )

        val request = UpdateTransactionRequest(
            amount = BigDecimal("75.50"),
            description = "Mercado atualizado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.of(2026, 5, 27),
            categoryId = categoryId
        )

        val updatedTransaction = currentTransaction.copy(
            amount = BigDecimal("75.50"),
            description = "Mercado atualizado",
            transactionDate = LocalDate.of(2026, 5, 27)
        )

        whenever(transactionRepository.findByIdAndUser(transactionId, user))
            .thenReturn(currentTransaction)

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = categoryId,
                user = user
            )
        ).thenReturn(category)

        whenever(transactionRepository.save(any()))
            .thenReturn(updatedTransaction)

        val response = transactionService.update(principal, transactionId, request)

        assertEquals(transactionId, response.id)
        assertEquals(BigDecimal("75.50"), response.amount)
        assertEquals("Mercado atualizado", response.description)
        assertEquals(LocalDate.of(2026, 5, 27), response.transactionDate)
    }
    @Test
    fun `should throw not found when updating non existing transaction`() {
        val transactionId = UUID.randomUUID()

        val request = UpdateTransactionRequest(
            amount = BigDecimal("75.50"),
            description = "Mercado atualizado"
        )

        whenever(transactionRepository.findByIdAndUser(transactionId, user))
            .thenReturn(null)

        assertThrows(BusinessException.NotFound::class.java) {
            transactionService.update(principal, transactionId, request)
        }
    }
    @Test
    fun `should throw bad request when updating transaction with incompatible category type`() {
        val transactionId = UUID.randomUUID()

        val expenseCategory = Category(
            id = UUID.randomUUID(),
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            user = user
        )

        val incomeCategory = Category(
            id = UUID.randomUUID(),
            name = "Salário",
            type = TransactionType.INCOME,
            user = user
        )

        val currentTransaction = Transaction(
            id = transactionId,
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now(),
            category = expenseCategory,
            user = user
        )

        val request = UpdateTransactionRequest(
            type = TransactionType.EXPENSE,
            categoryId = incomeCategory.id
        )

        whenever(transactionRepository.findByIdAndUser(transactionId, user))
            .thenReturn(currentTransaction)

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = requireNotNull(incomeCategory.id),
                user = user
            )
        ).thenReturn(incomeCategory)

        assertThrows(BusinessException.BadRequest::class.java) {
            transactionService.update(principal, transactionId, request)
        }
    }
    @Test
    fun `should throw bad request when transaction date is more than one year in future`() {
        val categoryId = UUID.randomUUID()

        val category = Category(
            id = categoryId,
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            user = user
        )

        val request = CreateTransactionRequest(
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.now().plusYears(2),
            categoryId = categoryId
        )

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = categoryId,
                user = user
            )
        ).thenReturn(category)

        assertThrows(BusinessException.BadRequest::class.java) {
            transactionService.create(principal, request)
        }
    }
}