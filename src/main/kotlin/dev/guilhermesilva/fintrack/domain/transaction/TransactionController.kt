package dev.guilhermesilva.fintrack.application.transaction

import dev.guilhermesilva.fintrack.domain.transaction.TransactionType
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
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

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateTransactionRequest
    ): TransactionResponse =
        transactionService.create(principal, request)

    @GetMapping
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

        @PageableDefault(size = 10, sort = ["transactionDate"])
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
    fun findById(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID
    ): TransactionResponse =
        transactionService.findById(principal, id)

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTransactionRequest
    ): TransactionResponse =
        transactionService.update(principal, id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: UUID
    ) {
        transactionService.delete(principal, id)
    }
}