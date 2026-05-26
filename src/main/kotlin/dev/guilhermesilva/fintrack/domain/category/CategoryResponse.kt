package dev.guilhermesilva.fintrack.application.category

import dev.guilhermesilva.fintrack.domain.category.Category
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import java.time.Instant
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val type: TransactionType,
    val active: Boolean,
    val createdAt: Instant?
)

fun Category.toResponse(): CategoryResponse =
    CategoryResponse(
        id = requireNotNull(id),
        name = name,
        type = type,
        active = active,
        createdAt = createdAt
    )