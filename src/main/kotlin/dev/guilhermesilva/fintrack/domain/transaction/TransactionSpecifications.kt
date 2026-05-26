package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.domain.transaction.Transaction
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.util.UUID

object TransactionSpecifications {

    fun byUserId(userId: UUID): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<Any>("user").get<UUID>("id"), userId)
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
                criteriaBuilder.equal(root.get<Any>("category").get<UUID>("id"), it)
            } ?: criteriaBuilder.conjunction()
        }

    fun byStartDate(startDate: LocalDate?): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            startDate?.let {
                criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), it)
            } ?: criteriaBuilder.conjunction()
        }

    fun byEndDate(endDate: LocalDate?): Specification<Transaction> =
        Specification { root, _, criteriaBuilder ->
            endDate?.let {
                criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), it)
            } ?: criteriaBuilder.conjunction()
        }
}