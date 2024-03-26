package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.reqeust.LoginRequestDTO
import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.dto.response.AccessTokenResponse
import com.jspl.tickettaka.dto.response.CheckMemberResponse
import com.jspl.tickettaka.dto.response.TicketResponse
import com.jspl.tickettaka.service.MemberService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberService: MemberService,

    @Value("\${loginData.secret.kakaoClientId}")
    private val kakaoClientId: String,

    @Value("\${loginData.secret.kakaoRedirectUri}")
    private val kakaoRedirectUri: String
) {

    @PostMapping("/signup")
    fun singUp(@RequestBody signUpRequestDTO: SignUpRequestDTO): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.signUp(signUpRequestDTO))
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequestDTO: LoginRequestDTO): ResponseEntity<AccessTokenResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.login(loginRequestDTO))
    }

    //회원탈퇴
    @DeleteMapping("/delete")
    fun delete(@AuthenticationPrincipal member: User): ResponseEntity<Unit> {
        val memberId = member.username.toLong()
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(memberService.deleteMember(memberId))
    }

    @GetMapping("/kakaologin")
    fun kakaoLogin(): RedirectView {
        val kakaoLoginUrl =
            "https://kauth.kakao.com/oauth/authorize?client_id=$kakaoClientId&redirect_uri=$kakaoRedirectUri&response_type=code"
        return RedirectView(kakaoLoginUrl)
    }

    @GetMapping("/getKakaoAccessToken")
    fun getKakaoAccessToken(@RequestParam("code") code: String): AccessTokenResponse {
        return memberService.getKakaoAccessToken(code)
    }

    //나의 예약한 티켓보기
    @GetMapping("/ticket")
    fun viewMyAllTicket(@AuthenticationPrincipal member: User): ResponseEntity<List<TicketResponse>> {
        val memberId = member.username.toLong()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.viewMyAllTicket(memberId))
    }


    ////////////////////////////////////[비지니스 로직 아님]///////////////////////////////////////////////

    /*
    //회원가입한 유저들 확인
    @GetMapping("/test/viewAllUserData")
    fun allViewUserData():ResponseEntity<List<CheckMemberResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.viewAllMemberData())
    }

    //유저 권한변경
    @PatchMapping("/memberRoleChange")
    fun memberRoleChange(@AuthenticationPrincipal member: User): ResponseEntity<String>{
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(memberService.memberRoleChange(member))
    }

    //인가 과정 확인용
    @PreAuthorize("hasAnyAuthority('TempNameConsumer')")
    @GetMapping("/test/checkMyPKValue")
    fun test(@AuthenticationPrincipal member: User):String {
        return member.username
    }
*/
}