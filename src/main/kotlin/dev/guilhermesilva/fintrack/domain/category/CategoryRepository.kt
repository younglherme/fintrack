package dev.guilhermesilva.fintrack.domain.category

import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CategoryRepository : JpaRepository<Category, UUID> {
    fun findAllByUserAndActiveTrue(user: User): List<Category>

    fun findAllByUserAndTypeAndActiveTrue(
        user: User,
        type: TransactionType
    ): List<Category>

    fun findByIdAndUserAndActiveTrue(
        id: UUID,
        user: User
    ): Category?

    fun existsByUserAndNameIgnoreCaseAndType(
        user: User,
        name: String,
        type: TransactionType
    ): Boolean
}