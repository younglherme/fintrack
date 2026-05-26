package dev.guilhermesilva.fintrack.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByEmailAndActiveTrue(email: String): User?
    fun existsByEmail(email: String): Boolean
}