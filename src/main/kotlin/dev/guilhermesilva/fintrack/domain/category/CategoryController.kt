package dev.guilhermesilva.fintrack.application.category

import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateCategoryRequest
    ): CategoryResponse =
        categoryService.create(principal, request)

    @GetMapping
    fun findAll(
        @AuthenticationPrincipal principal: UserPrincipal
    ): List<CategoryResponse> =
        categoryService.findAll(principal)

    @GetMapping("/{id}")
    fun findById(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID
    ): CategoryResponse =
        categoryService.findById(principal, id)
}