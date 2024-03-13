package com.jspl.tickettaka


import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.model.Member
import com.jspl.tickettaka.repository.MemberRepository
import com.jspl.tickettaka.service.MemberService
import com.jspl.tickettaka.service.TicketService
import jakarta.transaction.Transactional
import org.hibernate.validator.internal.util.Contracts.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


@SpringBootTest
//@Transactional
class SeatCheackTest(
    @Autowired
    private val memberRepository: MemberRepository,
    @Autowired
    private val ticketService: TicketService ,
    @Autowired
    private val memberService: MemberService
) {

    @Test
    @Transactional
    fun 회원가입(){
        val data = SignUpRequestDTO(
            email = "temp3@naver.com",
            password = "string",
            username =  "username1",
                role = "TempNameConsumer"
        )
        memberService.signUp(data)
    }

    @Test
    fun 회원수확인() {
        println( memberRepository.findAll().size)
    }

    @Test
    fun 화원이름변경(){
         var memberInfo =memberRepository.findByIdOrNull(2)!!

        println("=============================")
        println(memberInfo.username)
        println("=============================")

        val executors = Executors.newFixedThreadPool(4)
        val name = "string"
        val changeName = "changeUserName1"
    }

    @Test
    fun 좌석수확인(){

        val memberInfo = memberRepository.findByIdOrNull(1)!!.id!!

        val executors = Executors.newFixedThreadPool(4)

        val latch = CountDownLatch(4)

        executors.execute {
            val as1 = ticketService.ticketingTest(memberInfo, 100)
            latch.countDown()
        }

        executors.execute {
            val as2 = ticketService.ticketingTest(memberInfo, 100)
            latch.countDown()
        }

        executors.execute {
            val as3 = ticketService.ticketingTest(memberInfo, 100)
            latch.countDown()
        }

        executors.execute {
            val as4 = ticketService.ticketingTest(memberInfo, 100)
            latch.countDown()
        }
        latch.await()  // 일괄 종료시점을 맞추는 로직?



         val myTicketInfo = memberService.viewMyAllTicketTest(1)

        println("========================================")
        println("========================================")
        for(t in myTicketInfo) {
            println(t)
        }

        println("========================================")
        println("========================================")
    }
}