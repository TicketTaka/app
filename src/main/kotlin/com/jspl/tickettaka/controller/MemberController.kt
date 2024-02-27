package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.reqeust.LoginRequestDTO
import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.dto.response.AccessTokenResponse
import com.jspl.tickettaka.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class MemberController(
    private val memberService: MemberService
) {

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
}