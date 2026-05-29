package dev.guilhermesilva.fintrack.infra.security

import dev.guilhermesilva.fintrack.domain.user.User
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.UUID

class JwtAuthenticationFilterTest {

    private val jwtService: JwtService = mock()
    private val userDetailsService: UserDetailsService = mock()
    private val filterChain: FilterChain = mock()

    private val filter = JwtAuthenticationFilter(
        jwtService = jwtService,
        userDetailsService = userDetailsService
    )

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should continue filter chain when authorization header is missing`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        verify(jwtService, never()).extractUsername(any())
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `should continue filter chain when authorization header is not bearer`() {
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Basic invalid-token")
        }

        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        verify(jwtService, never()).extractUsername(any())
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `should authenticate user when token is valid`() {
        val token = "valid-token"
        val username = "gui@gmail.com"

        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer $token")
        }

        val response = MockHttpServletResponse()

        val user = User(
            id = UUID.randomUUID(),
            name = "Guilherme Silva",
            email = username,
            passwordHash = "hashed-password",
            active = true
        )

        val principal = UserPrincipal(user)

        whenever(jwtService.extractUsername(token))
            .thenReturn(username)

        whenever(userDetailsService.loadUserByUsername(username))
            .thenReturn(principal)

        whenever(jwtService.isTokenValid(token, principal))
            .thenReturn(true)

        filter.doFilter(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication

        assertEquals(principal, authentication.principal)
        assertTrue(authentication.isAuthenticated)

        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should not authenticate user when token is invalid`() {
        val token = "invalid-token"
        val username = "gui@gmail.com"

        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer $token")
        }

        val response = MockHttpServletResponse()

        val user = User(
            id = UUID.randomUUID(),
            name = "Guilherme Silva",
            email = username,
            passwordHash = "hashed-password",
            active = true
        )

        val principal = UserPrincipal(user)

        whenever(jwtService.extractUsername(token))
            .thenReturn(username)

        whenever(userDetailsService.loadUserByUsername(username))
            .thenReturn(principal)

        whenever(jwtService.isTokenValid(token, principal))
            .thenReturn(false)

        filter.doFilter(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)

        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should not authenticate when username cannot be extracted`() {
        val token = "invalid-token"

        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer $token")
        }

        val response = MockHttpServletResponse()

        whenever(jwtService.extractUsername(token))
            .thenReturn(null)

        filter.doFilter(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)

        verify(userDetailsService, never()).loadUserByUsername(any())
        verify(filterChain).doFilter(request, response)
    }
}