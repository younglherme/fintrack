package dev.guilhermesilva.fintrack.infra.exception

import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    private val request: HttpServletRequest = mock {
        on { requestURI }.thenReturn("/test")
    }

    @Test
    fun `should handle business not found exception`() {
        val exception = BusinessException.NotFound("Resource not found")

        val response = handler.handleBusinessException(exception, request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.status)
        assertEquals("Not Found", response.body?.error)
        assertEquals("Resource not found", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle business conflict exception`() {
        val exception = BusinessException.Conflict("Resource already exists")

        val response = handler.handleBusinessException(exception, request)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(409, response.body?.status)
        assertEquals("Conflict", response.body?.error)
        assertEquals("Resource already exists", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle business bad request exception`() {
        val exception = BusinessException.BadRequest("Invalid business rule")

        val response = handler.handleBusinessException(exception, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
        assertEquals("Bad Request", response.body?.error)
        assertEquals("Invalid business rule", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle response status exception`() {
        val exception = ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Item not found"
        )

        val response = handler.handleResponseStatusException(exception, request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.status)
        assertEquals("Not Found", response.body?.error)
        assertEquals("Item not found", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle bad credentials exception`() {
        val exception = BadCredentialsException("Bad credentials")

        val response = handler.handleBadCredentialsException(exception, request)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(401, response.body?.status)
        assertEquals("Unauthorized", response.body?.error)
        assertEquals("Invalid email or password", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle username not found exception`() {
        val exception = UsernameNotFoundException("User not found")

        val response = handler.handleUsernameNotFoundException(exception, request)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(401, response.body?.status)
        assertEquals("Unauthorized", response.body?.error)
        assertEquals("Invalid token or user not found", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle data integrity violation exception`() {
        val exception = DataIntegrityViolationException("Constraint violation")

        val response = handler.handleDataIntegrityViolationException(exception, request)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(409, response.body?.status)
        assertEquals("Conflict", response.body?.error)
        assertEquals("Data integrity violation", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle method argument type mismatch exception`() {
        val exception: MethodArgumentTypeMismatchException = mock()
        whenever(exception.name).thenReturn("type")

        val response = handler.handleMethodArgumentTypeMismatchException(exception, request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
        assertEquals("Bad Request", response.body?.error)
        assertEquals("Invalid value for parameter 'type'", response.body?.message)
        assertEquals("/test", response.body?.path)
    }

    @Test
    fun `should handle generic exception`() {
        val exception = RuntimeException("Unexpected error")

        val response = handler.handleGenericException(exception, request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(500, response.body?.status)
        assertEquals("Internal Server Error", response.body?.error)
        assertEquals("Unexpected internal server error", response.body?.message)
        assertEquals("/test", response.body?.path)
    }
}