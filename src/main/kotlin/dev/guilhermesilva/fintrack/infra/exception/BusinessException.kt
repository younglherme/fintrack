package dev.guilhermesilva.fintrack.infra.exception

import org.springframework.http.HttpStatus

sealed class BusinessException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message) {

    class NotFound(message: String) : BusinessException(
        status = HttpStatus.NOT_FOUND,
        message = message
    )

    class Conflict(message: String) : BusinessException(
        status = HttpStatus.CONFLICT,
        message = message
    )

    class BadRequest(message: String) : BusinessException(
        status = HttpStatus.BAD_REQUEST,
        message = message
    )

    class Unauthorized(message: String = "Unauthorized") : BusinessException(
        status = HttpStatus.UNAUTHORIZED,
        message = message
    )
}