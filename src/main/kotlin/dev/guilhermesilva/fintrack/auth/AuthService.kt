package dev.guilhermesilva.fintrack.application.auth

import dev.guilhermesilva.fintrack.domain.user.User
import dev.guilhermesilva.fintrack.domain.user.UserRepository
import dev.guilhermesilva.fintrack.infra.security.JwtService
import dev.guilhermesilva.fintrack.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val email = request.email.normalizedEmail()

        if (userRepository.existsByEmail(email)) {
            return AuthResponse.Error(
                message = "Email already registered",
                status = HttpStatus.CONFLICT
            )
        }

        val user = User(
            name = request.name.trim(),
            email = email,
            passwordHash = passwordEncoder.encode(request.password)
        )

        val savedUser = userRepository.save(user)
        val principal = UserPrincipal(savedUser)
        val token = jwtService.generateToken(principal)

        return AuthResponse.Success(
            token = token,
            user = savedUser.toAuthUserResponse()
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val email = request.email.normalizedEmail()

        return runCatching {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, request.password)
            )

            val principal = authentication.principal as UserPrincipal
            val token = jwtService.generateToken(principal)

            AuthResponse.Success(
                token = token,
                user = principal.user.toAuthUserResponse()
            )
        }.getOrElse { exception ->
            when (exception) {
                is BadCredentialsException -> AuthResponse.Error(
                    message = "Invalid email or password",
                    status = HttpStatus.UNAUTHORIZED
                )

                else -> AuthResponse.Error(
                    message = "Authentication failed",
                    status = HttpStatus.UNAUTHORIZED
                )
            }
        }
    }

    private fun String.normalizedEmail(): String =
        trim().lowercase()

    private fun User.toAuthUserResponse(): AuthUserResponse =
        AuthUserResponse(
            id = requireNotNull(id) { "User must have an id" },
            name = name,
            email = email
        )
}