package dev.guilhermesilva.fintrack.application.category

import dev.guilhermesilva.fintrack.domain.category.Category
import dev.guilhermesilva.fintrack.domain.category.CategoryRepository
import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.domain.user.User
import dev.guilhermesilva.fintrack.infra.exception.BusinessException
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.UUID

class CategoryServiceTest {

    private val categoryRepository: CategoryRepository = mock()

    private val categoryService = CategoryService(
        categoryRepository = categoryRepository
    )

    private val user = User(
        id = UUID.randomUUID(),
        name = "Guilherme Silva",
        email = "gui@gmail.com",
        passwordHash = "hashed-password"
    )

    private val principal = UserPrincipal(user)

    @Test
    fun `should create category successfully`() {
        val request = CreateCategoryRequest(
            name = " Alimentação ",
            type = TransactionType.EXPENSE
        )

        val savedCategory = Category(
            id = UUID.randomUUID(),
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            user = user
        )

        whenever(
            categoryRepository.existsByUserAndNameIgnoreCaseAndType(
                user = eq(user),
                name = eq("Alimentação"),
                type = eq(TransactionType.EXPENSE)
            )
        ).thenReturn(false)

        whenever(categoryRepository.save(any()))
            .thenReturn(savedCategory)

        val response = categoryService.create(principal, request)

        assertEquals(savedCategory.id, response.id)
        assertEquals("Alimentação", response.name)
        assertEquals(TransactionType.EXPENSE, response.type)
    }

    @Test
    fun `should throw conflict when category already exists`() {
        val request = CreateCategoryRequest(
            name = "Alimentação",
            type = TransactionType.EXPENSE
        )

        whenever(
            categoryRepository.existsByUserAndNameIgnoreCaseAndType(
                user = eq(user),
                name = eq("Alimentação"),
                type = eq(TransactionType.EXPENSE)
            )
        ).thenReturn(true)

        assertThrows(BusinessException.Conflict::class.java) {
            categoryService.create(principal, request)
        }
    }

    @Test
    fun `should find all active categories by user`() {
        val category = Category(
            id = UUID.randomUUID(),
            name = "Salário",
            type = TransactionType.INCOME,
            user = user
        )

        whenever(categoryRepository.findAllByUserAndActiveTrue(user))
            .thenReturn(listOf(category))

        val response = categoryService.findAll(principal)

        assertEquals(1, response.size)
        assertEquals("Salário", response.first().name)
        assertEquals(TransactionType.INCOME, response.first().type)
    }

    @Test
    fun `should find category by id`() {
        val categoryId = UUID.randomUUID()

        val category = Category(
            id = categoryId,
            name = "Freelance",
            type = TransactionType.INCOME,
            user = user
        )

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = categoryId,
                user = user
            )
        ).thenReturn(category)

        val response = categoryService.findById(principal, categoryId)

        assertEquals(categoryId, response.id)
        assertEquals("Freelance", response.name)
        assertEquals(TransactionType.INCOME, response.type)
    }

    @Test
    fun `should throw not found when category does not exist`() {
        val categoryId = UUID.randomUUID()

        whenever(
            categoryRepository.findByIdAndUserAndActiveTrue(
                id = categoryId,
                user = user
            )
        ).thenReturn(null)

        assertThrows(BusinessException.NotFound::class.java) {
            categoryService.findById(principal, categoryId)
        }
    }
}