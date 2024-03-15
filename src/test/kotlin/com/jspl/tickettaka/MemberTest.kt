package com.jspl.tickettaka

import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.model.Member
import com.jspl.tickettaka.model.enums.MemberRole
import com.jspl.tickettaka.repository.MemberRepository
import com.jspl.tickettaka.service.MemberService
import jakarta.transaction.Transactional
import org.awaitility.core.DurationFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.batch.core.configuration.DuplicateJobException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class MemberTest(
    @Autowired
    private val memberService: MemberService,
    @Autowired
    private val memberRepository: MemberRepository
) {


    @Test
    @Transactional
    fun 이메일중복체크() {

            val member = SignUpRequestDTO(
                email = "Temp3@naver.com",
                password = "string",
                username = "user1",
                role = "TempNameConsumer"
            )

            memberService.signUp(member)

            val member2 = SignUpRequestDTO(
                email = "Temp3@naver.com",
                password = "string",
                username = "user1",
                role = "TempNameConsumer"
            )

            memberService.signUp(member2)
        }

    @Test
    fun 회원가입한_유저정보_보기() {
        val allMemberInfo = memberRepository.findAll()
        for (m in allMemberInfo) {
            println(m.id)
            println(m.email)
            println(m.username)
        }
    }
}