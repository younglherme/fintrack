package dev.guilhermesilva.fintrack.application.category

import com.fasterxml.jackson.databind.ObjectMapper
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.domain.user.User
import dev.guilhermesilva.fintrack.infra.exception.BusinessException
import dev.guilhermesilva.fintrack.infra.exception.GlobalExceptionHandler
import dev.guilhermesilva.fintrack.infra.security.JwtAuthenticationFilter
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant
import java.util.UUID

@WebMvcTest(CategoryController::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler::class)
class CategoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var categoryService: CategoryService

    @MockitoBean
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    private lateinit var principal: UserPrincipal

    @BeforeEach
    fun setupSecurityContext() {
        principal = UserPrincipal(
            User(
                id = UUID.randomUUID(),
                name = "Guilherme Silva",
                email = "gui@gmail.com",
                passwordHash = "hashed-password"
            )
        )

        val authentication = UsernamePasswordAuthenticationToken(
            principal,
            null,
            principal.authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should create category successfully`() {
        val request = CreateCategoryRequest(
            name = "Alimentação",
            type = TransactionType.EXPENSE
        )

        val response = CategoryResponse(
            id = UUID.randomUUID(),
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            active = true,
            createdAt = Instant.parse("2026-05-26T13:00:00Z")
        )

        whenever(categoryService.create(any(), any()))
            .thenReturn(response)

        mockMvc.post("/categories") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { value(response.id.toString()) }
                jsonPath("$.name") { value("Alimentação") }
                jsonPath("$.type") { value("EXPENSE") }
                jsonPath("$.active") { value(true) }
            }
    }

    @Test
    fun `should return conflict when category already exists`() {
        val request = CreateCategoryRequest(
            name = "Alimentação",
            type = TransactionType.EXPENSE
        )

        whenever(categoryService.create(any(), any()))
            .thenThrow(BusinessException.Conflict("Category already exists for this type"))

        mockMvc.post("/categories") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isConflict() }
                jsonPath("$.status") { value(409) }
                jsonPath("$.error") { value("Conflict") }
                jsonPath("$.message") { value("Category already exists for this type") }
                jsonPath("$.path") { value("/categories") }
            }
    }

    @Test
    fun `should return bad request when create category request is invalid`() {
        val request = CreateCategoryRequest(
            name = "",
            type = null
        )

        mockMvc.post("/categories") {
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
    fun `should find all categories successfully`() {
        val response = listOf(
            CategoryResponse(
                id = UUID.randomUUID(),
                name = "Alimentação",
                type = TransactionType.EXPENSE,
                active = true,
                createdAt = Instant.parse("2026-05-26T13:00:00Z")
            ),
            CategoryResponse(
                id = UUID.randomUUID(),
                name = "Salário",
                type = TransactionType.INCOME,
                active = true,
                createdAt = Instant.parse("2026-05-26T13:00:00Z")
            )
        )

        whenever(categoryService.findAll(any()))
            .thenReturn(response)

        mockMvc.get("/categories")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
                jsonPath("$[0].name") { value("Alimentação") }
                jsonPath("$[0].type") { value("EXPENSE") }
                jsonPath("$[1].name") { value("Salário") }
                jsonPath("$[1].type") { value("INCOME") }
            }
    }

    @Test
    fun `should find category by id successfully`() {
        val categoryId = UUID.randomUUID()

        val response = CategoryResponse(
            id = categoryId,
            name = "Freelance",
            type = TransactionType.INCOME,
            active = true,
            createdAt = Instant.parse("2026-05-26T13:00:00Z")
        )

        whenever(categoryService.findById(any(), any()))
            .thenReturn(response)

        mockMvc.get("/categories/$categoryId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(categoryId.toString()) }
                jsonPath("$.name") { value("Freelance") }
                jsonPath("$.type") { value("INCOME") }
                jsonPath("$.active") { value(true) }
            }
    }

    @Test
    fun `should return not found when category does not exist`() {
        val categoryId = UUID.randomUUID()

        whenever(categoryService.findById(any(), any()))
            .thenThrow(BusinessException.NotFound("Category not found"))

        mockMvc.get("/categories/$categoryId")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
                jsonPath("$.error") { value("Not Found") }
                jsonPath("$.message") { value("Category not found") }
                jsonPath("$.path") { value("/categories/$categoryId") }
            }
    }
}