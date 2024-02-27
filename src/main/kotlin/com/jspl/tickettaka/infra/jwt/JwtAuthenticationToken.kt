package com.jspl.tickettaka.infra.jwt

import com.jspl.tickettaka.infra.UserPrincipal
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.web.authentication.WebAuthenticationDetails
import java.io.Serializable

class JwtAuthenticationToken(
    private val principal: UserPrincipal,
    // 요청한 Address 정보, sessionId등을 담음 (로깅 용도)
    details: WebAuthenticationDetails,
) : AbstractAuthenticationToken(principal.authorities), Serializable {

    init {
        // JWT 검증이 됐을시에 바로 생성할 예정이므로, 생성시 authenticated를 true로 설정
        super.setAuthenticated(true)
        super.setDetails(details)
    }

    override fun getPrincipal() = principal

    override fun getCredentials() = null

    override fun isAuthenticated(): Boolean {
        return true
    }

}