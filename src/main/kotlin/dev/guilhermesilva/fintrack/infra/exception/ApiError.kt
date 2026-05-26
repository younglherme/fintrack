package dev.guilhermesilva.fintrack.infra.exception

import java.time.Instant

data class ApiError(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val details: List<FieldErrorResponse> = emptyList()
)

data class FieldErrorResponse(
    val field: String,
    val message: String
)