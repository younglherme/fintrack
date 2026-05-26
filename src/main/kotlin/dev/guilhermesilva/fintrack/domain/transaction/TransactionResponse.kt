package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.domain.transaction.Transaction
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val amount: BigDecimal,
    val description: String,
    val type: TransactionType,
    val transactionDate: LocalDate,
    val category: TransactionCategoryResponse,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

data class TransactionCategoryResponse(
    val id: UUID,
    val name: String,
    val type: TransactionType
)

fun Transaction.toResponse(): TransactionResponse =
    TransactionResponse(
        id = requireNotNull(id),
        amount = amount,
        description = description,
        type = type,
        transactionDate = transactionDate,
        category = TransactionCategoryResponse(
            id = requireNotNull(category.id),
            name = category.name,
            type = category.type
        ),
        createdAt = createdAt,
        updatedAt = updatedAt
    )