package com.jspl.tickettaka.infra.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class JwtPlugin() {
    companion object {
        const val issuer = "티켓"
        const val secret = "PO4c8z41Hia5gJG3oeuFJMRYBB4Ws4aZ"
        const val accessTokenExpirationHour: Long = 168
    }

    fun validateToken(jwt: String): Result<Jws<Claims>> {
        return kotlin.runCatching {
            val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt)
        }
    }

    fun generateAccessToken(subject: String, role: String): String {
        return generateToken(subject, role, Duration.ofHours(accessTokenExpirationHour))
    }


    private fun generateToken(subject: String, role: String,expirationPeriod: Duration): String {
        val claims: Claims = Jwts.claims()
            .add(mapOf("role" to role)).build()

        val now = Instant.now()
        val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

        return Jwts.builder()
            .subject(subject)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationPeriod)))
            .claims(claims)
            .signWith(key)
            .compact()
    }

}