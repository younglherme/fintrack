package dev.guilhermesilva.fintrack.application.category

import dev.guilhermesilva.fintrack.application.common.normalizedText
import dev.guilhermesilva.fintrack.domain.category.Category
import dev.guilhermesilva.fintrack.domain.category.CategoryRepository
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    @Transactional
    fun create(
        principal: UserPrincipal,
        request: CreateCategoryRequest
    ): CategoryResponse {
        val type = requireNotNull(request.type)
        val name = request.name.normalizedText()

        if (
            categoryRepository.existsByUserAndNameIgnoreCaseAndType(
                user = principal.user,
                name = name,
                type = type
            )
        ) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Category already exists for this type"
            )
        }

        val category = Category(
            name = name,
            type = type,
            user = principal.user
        )

        return categoryRepository.save(category).toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(principal: UserPrincipal): List<CategoryResponse> =
        categoryRepository.findAllByUserAndActiveTrue(principal.user)
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(
        principal: UserPrincipal,
        id: UUID
    ): CategoryResponse =
        categoryRepository.findByIdAndUserAndActiveTrue(id, principal.user)
            ?.toResponse()
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Category not found"
            )
}