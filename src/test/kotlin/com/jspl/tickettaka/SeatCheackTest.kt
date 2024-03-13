package com.jspl.tickettaka


import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.infra.exception.ModelNotFoundException
import com.jspl.tickettaka.model.Member
import com.jspl.tickettaka.model.enums.MemberRole
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
    fun 가입된회원조회(){
       val test = memberService.viewAllMemberData()

        for(i in test){
            println(i.id)
            println(i.email)
            println(i.username)
            println()
        }
    }

    @Test
    fun 일괄회원삭제(){
        val allMemberId = memberRepository.findAll().map { it.id }
        for(m in allMemberId){
           val findMember =memberRepository.findByIdOrNull(m)!!
            memberRepository.delete(findMember)
        }
    }

    @Test
    fun 좌석수확인(){

        val allMemberInfo = memberRepository.findAll()
        val memberIdList :MutableList<Long> = mutableListOf()
        var count = 0

        while(allMemberInfo.size <4) {
            val data = Member(
                email = "temp" +count +"@naver.com",
                password = "string",
                username =  "username" +count,
                role = MemberRole.TempNameConsumer
            )

            val memberEmail = memberRepository.findByEmail(data.email)
            if(memberEmail == null){
                memberRepository.save(data)
            }

            count++


        }

        for(m in allMemberInfo){
            memberIdList.add(m.id!!)
        }

        val memberInfo = memberRepository.findByIdOrNull(memberIdList[0])!!.id!!
        val member2Info = memberRepository.findByIdOrNull(memberIdList[1])!!.id!!
        val member3Info = memberRepository.findByIdOrNull(memberIdList[2])!!.id!!
        val member4Info = memberRepository.findByIdOrNull(memberIdList[3])!!.id!!


        val executors = Executors.newFixedThreadPool(4)

        val latch = CountDownLatch(4)

        executors.execute {
            ticketService.ticketing(memberInfo, 100)
            latch.countDown()
        }

        executors.execute {
            ticketService.ticketing(member2Info, 100)
            latch.countDown()
        }

        executors.execute {
            ticketService.ticketing(member3Info, 100)
            latch.countDown()
        }

        executors.execute {
            ticketService.ticketing(member4Info, 100)
            latch.countDown()
        }
        latch.await()  // 일괄 종료시점을 맞추는 로직?

         val myTicketInfo = memberService.viewMyAllTicket(1)

        println("========================================")
        println("========================================")
        for(t in myTicketInfo) {
            println(t)
        }

        println("========================================")
        println("========================================")
    }
}