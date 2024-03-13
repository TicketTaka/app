package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.reqeust.LoginRequestDTO
import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.dto.response.AccessTokenResponse
import com.jspl.tickettaka.dto.response.CheckMemberResponse
import com.jspl.tickettaka.dto.response.TicketResponse
import com.jspl.tickettaka.infra.jwt.JwtPlugin
import com.jspl.tickettaka.service.MemberService
import org.springframework.http.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberService: MemberService
) {

    val kakaoClientId = "60ffdbd138489440034b2e2bb1f592e3"
    val kakaoRedirectUri = "http://localhost:8080/api/members/getKakaoAccessToken"

    @PostMapping("/signup")
    fun singUp(@RequestBody signUpRequestDTO: SignUpRequestDTO):ResponseEntity<*>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.signUp(signUpRequestDTO))
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequestDTO: LoginRequestDTO):ResponseEntity<AccessTokenResponse>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.login(loginRequestDTO))
    }
    @GetMapping("/kakaologin")
    fun kakaoLogin(): RedirectView {
        val kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=$kakaoClientId&redirect_uri=$kakaoRedirectUri&response_type=code"
        return RedirectView(kakaoLoginUrl)
    }

    @GetMapping("/getKakaoAccessToken")
    fun getKakaoAccessToken(@RequestParam("code") code: String): AccessTokenResponse {
        return memberService.getKakaoAccessToken(code)
    }

    @GetMapping("/ticket")
    fun viewMyAllTicket(@AuthenticationPrincipal member: User) : ResponseEntity<List<TicketResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.viewMyAllTicket(member))
    }

//    @PatchMapping("/memberRoleChange")
//    fun memberRoleChange(@AuthenticationPrincipal member: User): ResponseEntity<String>{
//        return ResponseEntity.status(HttpStatus.OK).body(memberService.memberRoleChange(member))
//    }
    @GetMapping("/test/viewAllUserData")
    fun allViewUserData():ResponseEntity<List<CheckMemberResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.viewAllMemberData())
    }
//
//    @PreAuthorize("hasAnyAuthority('TempNameConsumer')")
//    @GetMapping("/test/checkMyPKValue")
//    fun test(@AuthenticationPrincipal member: User):String {
//        return member.username
//    }

}