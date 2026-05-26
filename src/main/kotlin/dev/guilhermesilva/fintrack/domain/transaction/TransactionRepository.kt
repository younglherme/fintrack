package dev.guilhermesilva.fintrack.domain.transaction

import dev.guilhermesilva.fintrack.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.UUID

interface TransactionRepository :
    JpaRepository<Transaction, UUID>,
    JpaSpecificationExecutor<Transaction> {

    fun findByIdAndUser(
        id: UUID,
        user: User
    ): Transaction?
}