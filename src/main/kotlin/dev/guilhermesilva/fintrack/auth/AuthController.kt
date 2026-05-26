package dev.guilhermesilva.fintrack.application.auth

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "Registro e login de usuários")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): AuthResponse.Success =
        authService.register(request)

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): AuthResponse.Success =
        authService.login(request)
}