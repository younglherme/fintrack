package dev.guilhermesilva.fintrack.infra.security

import dev.guilhermesilva.fintrack.domain.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val normalizedEmail = username.trim().lowercase()

        val user = userRepository.findByEmailAndActiveTrue(normalizedEmail)
            ?: throw UsernameNotFoundException("Usuario não encontrado")

        return UserPrincipal(user)
    }
}