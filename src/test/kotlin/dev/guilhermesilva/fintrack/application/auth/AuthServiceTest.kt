package dev.guilhermesilva.fintrack.application.auth

import dev.guilhermesilva.fintrack.domain.user.User
import dev.guilhermesilva.fintrack.domain.user.UserRepository
import dev.guilhermesilva.fintrack.infra.exception.BusinessException
import dev.guilhermesilva.fintrack.infra.security.JwtService
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

class AuthServiceTest {

    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val jwtService: JwtService = mock()
    private val authenticationManager: AuthenticationManager = mock()

    private val authService = AuthService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder,
        jwtService = jwtService,
        authenticationManager = authenticationManager
    )

    @Test
    fun `should register user successfully`() {
        val request = RegisterRequest(
            name = "Guilherme Silva",
            email = "GUI@GMAIL.COM",
            password = "12345678"
        )

        val savedUser = User(
            id = UUID.randomUUID(),
            name = "Guilherme Silva",
            email = "gui@gmail.com",
            passwordHash = "hashed-password"
        )

        whenever(userRepository.existsByEmail("gui@gmail.com"))
            .thenReturn(false)

        whenever(passwordEncoder.encode("12345678"))
            .thenReturn("hashed-password")

        whenever(userRepository.save(any()))
            .thenReturn(savedUser)

        whenever(jwtService.generateToken(any()))
            .thenReturn("fake-jwt-token")

        val response = authService.register(request)

        assertEquals("fake-jwt-token", response.token)
        assertEquals("Bearer", response.tokenType)
        assertEquals(savedUser.id, response.user.id)
        assertEquals("Guilherme Silva", response.user.name)
        assertEquals("gui@gmail.com", response.user.email)
    }

    @Test
    fun `should throw conflict when email already exists`() {
        val request = RegisterRequest(
            name = "Guilherme Silva",
            email = "gui@gmail.com",
            password = "12345678"
        )

        whenever(userRepository.existsByEmail("gui@gmail.com"))
            .thenReturn(true)

        assertThrows(BusinessException.Conflict::class.java) {
            authService.register(request)
        }
    }

    @Test
    fun `should login successfully`() {
        val user = User(
            id = UUID.randomUUID(),
            name = "Guilherme Silva",
            email = "gui@gmail.com",
            passwordHash = "hashed-password"
        )

        val principal = UserPrincipal(user)

        val authentication: Authentication = mock()

        whenever(authentication.principal)
            .thenReturn(principal)

        whenever(
            authenticationManager.authenticate(
                any<UsernamePasswordAuthenticationToken>()
            )
        ).thenReturn(authentication)

        whenever(jwtService.generateToken(eq(principal)))
            .thenReturn("fake-login-token")

        val request = LoginRequest(
            email = "GUI@GMAIL.COM",
            password = "12345678"
        )

        val response = authService.login(request)

        assertEquals("fake-login-token", response.token)
        assertEquals(user.id, response.user.id)
        assertEquals("Guilherme Silva", response.user.name)
        assertEquals("gui@gmail.com", response.user.email)
    }
}