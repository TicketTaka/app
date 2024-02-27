package com.jspl.tickettaka.infra.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter (
    private val jwtPlugin: JwtPlugin
): OncePerRequestFilter(){

    companion object{
        private val BEARER_PATTERN = Regex("^Bearer (.+?)$")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = request.getBearerToken()


        if(jwt != null){

            jwtPlugin.validateToken(jwt)
                .onSuccess {
                    val userId = it.payload.subject
                    val role = it.payload.get("role", String::class.java)
                    val user = User(userId,"",listOf(SimpleGrantedAuthority(role)))

                    UsernamePasswordAuthenticationToken.authenticated(user, jwt, user.authorities)
                        .also { SecurityContextHolder.getContext().authentication = it }
                }


        }
        filterChain.doFilter(request,response)
    }


    //오류가 발생 되었을 경우 여기로 오도록
    private fun HttpServletRequest.getBearerToken():String?{
        val headerValue = this.getHeader(HttpHeaders.AUTHORIZATION) ?:return null
        return BEARER_PATTERN.find(headerValue)?.groupValues?.get(1)
    }
}