package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.domain.category.Category
import dev.guilhermesilva.fintrack.domain.transaction.Transaction
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.domain.user.User
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.util.UUID

object TransactionSpecifications {

    fun byUserId(userId: UUID): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            val userJoin = root.join<Transaction, User>("user", JoinType.INNER)
            criteriaBuilder.equal(userJoin.get<UUID>("id"), userId)
        }

    fun byType(type: TransactionType?): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            type?.let {
                criteriaBuilder.equal(root.get<TransactionType>("type"), it)
            } ?: criteriaBuilder.conjunction()
        }

    fun byCategoryId(categoryId: UUID?): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            categoryId?.let {
                val categoryJoin = root.join<Transaction, Category>("category", JoinType.INNER)
                criteriaBuilder.equal(categoryJoin.get<UUID>("id"), it)
            } ?: criteriaBuilder.conjunction()
        }

    fun byStartDate(startDate: LocalDate?): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            startDate?.let {
                criteriaBuilder.greaterThanOrEqualTo(
                    root.get("transactionDate"),
                    it
                )
            } ?: criteriaBuilder.conjunction()
        }

    fun byEndDate(endDate: LocalDate?): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            endDate?.let {
                criteriaBuilder.lessThanOrEqualTo(
                    root.get("transactionDate"),
                    it
                )
            } ?: criteriaBuilder.conjunction()
        }
}