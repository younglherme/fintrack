package dev.guilhermesilva.fintrack.application.auth

import java.util.UUID

sealed class AuthResponse {

    data class Success(
        val token: String,
        val tokenType: String = "Bearer",
        val user: AuthUserResponse
    ) : AuthResponse()
}

data class AuthUserResponse(
    val id: UUID,
    val name: String,
    val email: String
)