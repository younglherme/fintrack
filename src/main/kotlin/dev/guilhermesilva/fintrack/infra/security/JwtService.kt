package dev.guilhermesilva.fintrack.infra.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {

    private val secretKey: SecretKey by lazy {
        val keyBytes = Base64.getDecoder().decode(jwtProperties.secret)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(userPrincipal: UserPrincipal): String {
        val now = Instant.now()
        val expiration = now.plusMillis(jwtProperties.expirationInMs)

        return Jwts.builder()
            .subject(userPrincipal.username)
            .claim("userId", userPrincipal.id.toString())
            .claim("name", userPrincipal.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey)
            .compact()
    }

    fun extractUsername(token: String): String? =
        runCatching {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        }.getOrNull()

    fun isTokenValid(
        token: String,
        userPrincipal: UserPrincipal
    ): Boolean {
        val username = extractUsername(token)

        return username == userPrincipal.username &&
                !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean =
        runCatching {
            val expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .expiration

            expiration.before(Date())
        }.getOrDefault(true)
}