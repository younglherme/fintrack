package dev.guilhermesilva.fintrack.infra.security

import dev.guilhermesilva.fintrack.domain.user.User
import dev.guilhermesilva.fintrack.domain.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.UUID

class CustomUserDetailsServiceTest {

    private val userRepository: UserRepository = mock()

    private val service = CustomUserDetailsService(
        userRepository = userRepository
    )

    @Test
    fun `should load user by username successfully`() {
        val user = User(
            id = UUID.randomUUID(),
            name = "Guilherme Silva",
            email = "gui@gmail.com",
            passwordHash = "hashed-password",
            active = true
        )

        whenever(userRepository.findByEmailAndActiveTrue("gui@gmail.com"))
            .thenReturn(user)

        val userDetails = service.loadUserByUsername(" GUI@GMAIL.COM ")

        assertEquals("gui@gmail.com", userDetails.username)
        assertEquals("hashed-password", userDetails.password)
        assertTrue(userDetails.isEnabled)
        assertTrue(userDetails.authorities.any { it.authority == "ROLE_USER" })
    }

    @Test
    fun `should throw username not found when user does not exist`() {
        whenever(userRepository.findByEmailAndActiveTrue("notfound@gmail.com"))
            .thenReturn(null)

        assertThrows(UsernameNotFoundException::class.java) {
            service.loadUserByUsername("notfound@gmail.com")
        }
    }
}