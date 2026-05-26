package dev.guilhermesilva.fintrack.application.category

import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Categorias", description = "Gerenciamento de categorias financeiras")
@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar uma categoria")
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateCategoryRequest
    ): CategoryResponse =
        categoryService.create(principal, request)

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    fun findAll(
        @AuthenticationPrincipal principal: UserPrincipal
    ): List<CategoryResponse> =
        categoryService.findAll(principal)

    @GetMapping("/{id}")
    @Operation(summary = "Listar todas as categorias por ID")
    fun findById(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID
    ): CategoryResponse =
        categoryService.findById(principal, id)
}