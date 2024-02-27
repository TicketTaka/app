package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.LoginRequestDTO
import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.dto.response.MemberResponse
import com.jspl.tickettaka.infra.exception.ErrorResponse
import com.jspl.tickettaka.infra.jwt.JwtPlugin
import com.jspl.tickettaka.model.Member
import com.jspl.tickettaka.model.enums.MemberRole
import com.jspl.tickettaka.model.toResponse
import com.jspl.tickettaka.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin
    ) {

    fun signUp(request: SignUpRequestDTO): MemberResponse {
        val (email, username, password, role) = request

        //이메일 형식 확인

        //이메일 중복확인
        if (findByEmail(email) != null) {
            throw IllegalArgumentException("이미 존재하는 email 입니다")
        }

        //유저 db에 저장
        val memberData = saveMember(request)

        //트랜잭션 내부라면 작성하지 않아도 되는 로직
        val saveMember = memberRepository.save(memberData)
        val MappingMember = saveMember.toResponse()
        return MappingMember
    }


    fun login(request:LoginRequestDTO):String{
        val (email, password) = request
        val foundMember = findByEmail(email)

        if (foundMember == null && passwordEncoder.matches(password,foundMember)) {
            throw IllegalArgumentException("Email 또는 Password 가 잘못 입력되었습니다")
        }


        val data = jwtPlugin.generateAccessToken(
            subject = foundMember!!.id.toString(),
            role = foundMember.role.name
        )





    }

    private fun findByEmail(email: String): Member? {
        return memberRepository.findByEmail(email)
    }
    private fun saveMember(request:SignUpRequestDTO):Member{
        val (email, username, password, role) = request

        val userData = Member(
            email = email,
            username = username,
            password = passwordEncoder.encode(password),

            role = when (role) {
                "TempNameProducer" -> MemberRole.TempNameProducer
                "TempNameConsume" -> MemberRole.TempNameConsumer
                else -> throw IllegalArgumentException("잘못된 접근입니다")
            }

        )

        return userData
    }



}