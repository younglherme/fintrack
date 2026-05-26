package dev.guilhermesilva.fintrack.infra.security

import dev.guilhermesilva.fintrack.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class UserPrincipal(
    val user: User
) : UserDetails {

    val id: UUID
        get() = requireNotNull(user.id)

    val name: String
        get() = user.name

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun getPassword(): String =
        user.passwordHash

    override fun getUsername(): String =
        user.email

    override fun isAccountNonExpired(): Boolean =
        true

    override fun isAccountNonLocked(): Boolean =
        true

    override fun isCredentialsNonExpired(): Boolean =
        true

    override fun isEnabled(): Boolean =
        user.active
}