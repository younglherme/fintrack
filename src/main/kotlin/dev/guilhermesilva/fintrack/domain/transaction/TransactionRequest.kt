package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateTransactionRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    val amount: BigDecimal?,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = 150, message = "Description must have at most 150 characters")
    val description: String,

    @field:NotNull(message = "Transaction type is required")
    val type: TransactionType?,

    @field:NotNull(message = "Transaction date is required")
    val transactionDate: LocalDate?,

    @field:NotNull(message = "Category id is required")
    val categoryId: UUID?
)

data class UpdateTransactionRequest(
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    val amount: BigDecimal? = null,

    @field:Size(max = 150, message = "Description must have at most 150 characters")
    val description: String? = null,

    val type: TransactionType? = null,

    val transactionDate: LocalDate? = null,

    val categoryId: UUID? = null
)