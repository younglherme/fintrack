package dev.guilhermesilva.fintrack.application.transaction

import org.mockito.kotlin.isNull
import org.springframework.data.domain.Pageable
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@WebMvcTest(TransactionController::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler::class)
class TransactionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var transactionService: TransactionService

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

        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.authorities
            )
    }

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should create transaction successfully`() {
        val categoryId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()

        val request = CreateTransactionRequest(
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.of(2026, 5, 26),
            categoryId = categoryId
        )

        val response = transactionResponse(
            transactionId = transactionId,
            categoryId = categoryId,
            categoryName = "Alimentação"
        )

        whenever(transactionService.create(any(), any()))
            .thenReturn(response)

        mockMvc.post("/transactions") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id") { value(transactionId.toString()) }
                jsonPath("$.amount") { value(59.90) }
                jsonPath("$.description") { value("Mercado") }
                jsonPath("$.type") { value("EXPENSE") }
                jsonPath("$.transactionDate") { value("2026-05-26") }
                jsonPath("$.category.id") { value(categoryId.toString()) }
                jsonPath("$.category.name") { value("Alimentação") }
            }
    }

    @Test
    fun `should return bad request when create transaction request is invalid`() {
        val request = CreateTransactionRequest(
            amount = BigDecimal("0.00"),
            description = "",
            type = null,
            transactionDate = null,
            categoryId = null
        )

        mockMvc.post("/transactions") {
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
    fun `should find all transactions successfully`() {
        val categoryId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()

        val response = transactionResponse(
            transactionId = transactionId,
            categoryId = categoryId,
            categoryName = "Alimentação"
        )

        val page = PageImpl(
            listOf(response),
            PageRequest.of(0, 10),
            1
        )

        whenever(
            transactionService.findAll(
                any(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any<Pageable>()
            )
        ).thenReturn(page)

        mockMvc.get("/transactions") {
            param("page", "0")
            param("size", "10")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.content.length()") { value(1) }
                jsonPath("$.content[0].id") { value(transactionId.toString()) }
                jsonPath("$.content[0].description") { value("Mercado") }
                jsonPath("$.content[0].type") { value("EXPENSE") }
                jsonPath("$.totalElements") { value(1) }
                jsonPath("$.size") { value(10) }
                jsonPath("$.number") { value(0) }
            }
    }

    @Test
    fun `should find transaction by id successfully`() {
        val categoryId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()

        val response = transactionResponse(
            transactionId = transactionId,
            categoryId = categoryId,
            categoryName = "Alimentação"
        )

        whenever(transactionService.findById(any(), any()))
            .thenReturn(response)

        mockMvc.get("/transactions/$transactionId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(transactionId.toString()) }
                jsonPath("$.amount") { value(59.90) }
                jsonPath("$.description") { value("Mercado") }
                jsonPath("$.type") { value("EXPENSE") }
                jsonPath("$.category.id") { value(categoryId.toString()) }
            }
    }

    @Test
    fun `should return not found when transaction does not exist`() {
        val transactionId = UUID.randomUUID()

        whenever(transactionService.findById(any(), any()))
            .thenThrow(BusinessException.NotFound("Transaction not found"))

        mockMvc.get("/transactions/$transactionId")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
                jsonPath("$.error") { value("Not Found") }
                jsonPath("$.message") { value("Transaction not found") }
                jsonPath("$.path") { value("/transactions/$transactionId") }
            }
    }

    @Test
    fun `should update transaction successfully`() {
        val categoryId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()

        val request = UpdateTransactionRequest(
            amount = BigDecimal("75.50"),
            description = "Mercado atualizado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.of(2026, 5, 27),
            categoryId = categoryId
        )

        val response = transactionResponse(
            transactionId = transactionId,
            categoryId = categoryId,
            amount = BigDecimal("75.50"),
            description = "Mercado atualizado",
            transactionDate = LocalDate.of(2026, 5, 27),
            categoryName = "Alimentação"
        )

        whenever(transactionService.update(any(), any(), any()))
            .thenReturn(response)

        mockMvc.put("/transactions/$transactionId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(transactionId.toString()) }
                jsonPath("$.amount") { value(75.50) }
                jsonPath("$.description") { value("Mercado atualizado") }
                jsonPath("$.transactionDate") { value("2026-05-27") }
            }
    }

    @Test
    fun `should delete transaction successfully`() {
        val transactionId = UUID.randomUUID()

        mockMvc.delete("/transactions/$transactionId")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `should return bad request when category type does not match transaction type`() {
        val categoryId = UUID.randomUUID()

        val request = CreateTransactionRequest(
            amount = BigDecimal("59.90"),
            description = "Mercado",
            type = TransactionType.EXPENSE,
            transactionDate = LocalDate.of(2026, 5, 26),
            categoryId = categoryId
        )

        whenever(transactionService.create(any(), any()))
            .thenThrow(BusinessException.BadRequest("Category type must match transaction type"))

        mockMvc.post("/transactions") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Category type must match transaction type") }
            }
    }

    private fun transactionResponse(
        transactionId: UUID = UUID.randomUUID(),
        categoryId: UUID = UUID.randomUUID(),
        amount: BigDecimal = BigDecimal("59.90"),
        description: String = "Mercado",
        type: TransactionType = TransactionType.EXPENSE,
        transactionDate: LocalDate = LocalDate.of(2026, 5, 26),
        categoryName: String = "Alimentação"
    ): TransactionResponse =
        TransactionResponse(
            id = transactionId,
            amount = amount,
            description = description,
            type = type,
            transactionDate = transactionDate,
            category = TransactionCategoryResponse(
                id = categoryId,
                name = categoryName,
                type = type
            ),
            createdAt = Instant.parse("2026-05-26T13:00:00Z"),
            updatedAt = Instant.parse("2026-05-26T13:00:00Z")
        )
}