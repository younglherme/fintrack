package dev.guilhermesilva.fintrack.infra.security

import dev.guilhermesilva.fintrack.domain.user.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class JwtServiceTest {

    private val jwtProperties = JwtProperties(
        secret = "ZmZpbm5UcmFja0FwaVNlY3JldEtleUZvckp3dEF1dGhTdHJvbmdFbm91Z2gyMDI2",
        expirationInMs = 3600000
    )

    private val jwtService = JwtService(jwtProperties)

    private val user = User(
        id = UUID.randomUUID(),
        name = "Guilherme Silva",
        email = "gui@gmail.com",
        passwordHash = "hashed-password"
    )

    private val principal = UserPrincipal(user)

    @Test
    fun `should generate token successfully`() {
        val token = jwtService.generateToken(principal)

        assertNotNull(token)
        assertTrue(token.isNotBlank())
    }

    @Test
    fun `should extract username from valid token`() {
        val token = jwtService.generateToken(principal)

        val username = jwtService.extractUsername(token)

        assertEquals("gui@gmail.com", username)
    }

    @Test
    fun `should validate valid token`() {
        val token = jwtService.generateToken(principal)

        val isValid = jwtService.isTokenValid(token, principal)

        assertTrue(isValid)
    }

    @Test
    fun `should return false when token belongs to another user`() {
        val token = jwtService.generateToken(principal)

        val anotherUser = User(
            id = UUID.randomUUID(),
            name = "Maria Oliveira",
            email = "maria@gmail.com",
            passwordHash = "hashed-password"
        )

        val anotherPrincipal = UserPrincipal(anotherUser)

        val isValid = jwtService.isTokenValid(token, anotherPrincipal)

        assertFalse(isValid)
    }

    @Test
    fun `should return null when token is invalid`() {
        val username = jwtService.extractUsername("invalid-token")

        assertEquals(null, username)
    }
}