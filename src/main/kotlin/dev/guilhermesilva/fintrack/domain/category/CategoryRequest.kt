package dev.guilhermesilva.fintrack.application.category

import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateCategoryRequest(
    @field:NotBlank(message = "Category name is required")
    @field:Size(max = 80, message = "Category name must have at most 80 characters")
    val name: String,

    @field:NotNull(message = "Category type is required")
    val type: TransactionType?
)