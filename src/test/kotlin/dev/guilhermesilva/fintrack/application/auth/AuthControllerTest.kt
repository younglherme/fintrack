package dev.guilhermesilva.fintrack.application.auth

import com.fasterxml.jackson.databind.ObjectMapper
import dev.guilhermesilva.fintrack.infra.exception.BusinessException
import dev.guilhermesilva.fintrack.infra.exception.GlobalExceptionHandler
import dev.guilhermesilva.fintrack.infra.security.JwtAuthenticationFilter
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var authService: AuthService

    @MockitoBean
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Test
    fun `should register user successfully`() {
        val request = RegisterRequest(
            name = "Guilherme Silva",
            email = "gui@gmail.com",
            password = "12345678"
        )

        val response = AuthResponse.Success(
            token = "fake-jwt-token",
            user = AuthUserResponse(
                id = UUID.randomUUID(),
                name = "Guilherme Silva",
                email = "gui@gmail.com"
            )
        )

        whenever(authService.register(any()))
            .thenReturn(response)

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.token") { value("fake-jwt-token") }
                jsonPath("$.tokenType") { value("Bearer") }
                jsonPath("$.user.name") { value("Guilherme Silva") }
                jsonPath("$.user.email") { value("gui@gmail.com") }
            }
    }

    @Test
    fun `should return conflict when email already exists`() {
        val request = RegisterRequest(
            name = "Guilherme Silva",
            email = "gui@gmail.com",
            password = "12345678"
        )

        whenever(authService.register(any()))
            .thenThrow(BusinessException.Conflict("Email already registered"))

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isConflict() }
                jsonPath("$.status") { value(409) }
                jsonPath("$.error") { value("Conflict") }
                jsonPath("$.message") { value("Email already registered") }
                jsonPath("$.path") { value("/auth/register") }
            }
    }

    @Test
    fun `should return bad request when register request is invalid`() {
        val request = RegisterRequest(
            name = "",
            email = "email-invalido",
            password = "123"
        )

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Validation failed") }
            }
    }

    @Test
    fun `should login successfully`() {
        val request = LoginRequest(
            email = "gui@gmail.com",
            password = "12345678"
        )

        val response = AuthResponse.Success(
            token = "fake-login-token",
            user = AuthUserResponse(
                id = UUID.randomUUID(),
                name = "Guilherme Silva",
                email = "gui@gmail.com"
            )
        )

        whenever(authService.login(any()))
            .thenReturn(response)

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.token") { value("fake-login-token") }
                jsonPath("$.tokenType") { value("Bearer") }
                jsonPath("$.user.name") { value("Guilherme Silva") }
                jsonPath("$.user.email") { value("gui@gmail.com") }
            }
    }

    @Test
    fun `should return bad request when login request is invalid`() {
        val request = LoginRequest(
            email = "email-invalido",
            password = ""
        )

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Validation failed") }
            }
    }
}