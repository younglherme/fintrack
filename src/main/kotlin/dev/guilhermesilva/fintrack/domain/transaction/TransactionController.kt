package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@Tag(name = "Transações", description = "Gerenciamento de receitas e despesas")
@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar nova transação")
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateTransactionRequest
    ): TransactionResponse =
        transactionService.create(principal, request)

    @GetMapping
    @Operation(summary = "Localizar transações")
    fun findAll(
        @AuthenticationPrincipal principal: UserPrincipal,

        @RequestParam(required = false)
        type: TransactionType?,

        @RequestParam(required = false)
        categoryId: UUID?,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,

        @PageableDefault(size = 10)
        pageable: Pageable
    ): Page<TransactionResponse> =
        transactionService.findAll(
            principal = principal,
            type = type,
            categoryId = categoryId,
            startDate = startDate,
            endDate = endDate,
            pageable = pageable
        )

    @GetMapping("/{id}")
    @Operation(summary = "Localizar transação por ID")
    fun findById(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID
    ): TransactionResponse =
        transactionService.findById(principal, id)

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação por ID")
    fun update(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTransactionRequest
    ): TransactionResponse =
        transactionService.update(principal, id, request)

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar transação por ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID
    ) {
        transactionService.delete(principal, id)
    }
}