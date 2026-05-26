package dev.guilhermesilva.fintrack.application.auth

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

sealed class AuthResponse {

    data class Success(
        val token: String,
        val tokenType: String = "Bearer",
        val user: AuthUserResponse
    ) : AuthResponse()

    data class Error(
        val message: String,

        @JsonIgnore
        val status: HttpStatus = HttpStatus.BAD_REQUEST
    ) : AuthResponse()
}

data class AuthUserResponse(
    val id: UUID,
    val name: String,
    val email: String
)

fun AuthResponse.toResponseEntity(successStatus: HttpStatus = HttpStatus.OK): ResponseEntity<AuthResponse> =
    when (this) {
        is AuthResponse.Success -> ResponseEntity.status(successStatus).body(this)
        is AuthResponse.Error -> ResponseEntity.status(status).body(this)
    }