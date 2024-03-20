package com.jspl.tickettaka


import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.dto.response.CheckMemberResponse
import com.jspl.tickettaka.infra.exception.ModelNotFoundException
import com.jspl.tickettaka.model.*
import com.jspl.tickettaka.model.enums.MemberRole
import com.jspl.tickettaka.repository.*
import com.jspl.tickettaka.service.MemberService
import com.jspl.tickettaka.service.TicketService
import jakarta.transaction.Transactional
import org.hibernate.validator.internal.util.Contracts.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


@SpringBootTest
@Transactional
//@Transactional
class SeatCheackTest(
    @Autowired
    private val facilityDetailRepository: FacilityDetailRepository,
    @Autowired
    private val facilityInstanceRepository: FacilityInstanceRepository,
    @Autowired
    private val facilityRepository: FacilityRepository,
    @Autowired
    private val memberRepository: MemberRepository,
    @Autowired
    private val ticketService: TicketService,
    @Autowired
    private val ticketRepository: TicketRepository,
    @Autowired
    private val memberService: MemberService,
    @Autowired
    private val performanceInstanceRepository: PerformanceInstanceRepository,
    @Autowired
    private val seatInfoRepository: SeatInfoRepository
) {


    private fun 티켓예약(memberId: Long, seatId: Long): Ticket {
        val seatInfo = seatInfoRepository.findWithLockById(seatId)
            ?: throw ModelNotFoundException("SeatInfo", seatId)

        val performanceInstanceId = seatInfo.performanceInstance.performanceInstanceId
        val performanceInstanceInfo = performanceInstanceFindById(performanceInstanceId)

        val ticket = Ticket(
            memberId = memberId,                                                     //나의 정보
            performanceInstanceId = performanceInstanceId,                           //공연Id
            performanceName = seatInfo.performanceInstance.performanceName,          //공연이름
            priceInfo = if (seatInfo.price != "") seatInfo.price else "전석무료",                                          //금액
            seatId = seatInfo.id,                                                    //좌석Id
            setInfo = seatInfo.seatNumber.toString(),                                //좌석번호
            reservedTime = seatInfo.performanceInstance.date.toString()              //예매된 시간
        )

        if (seatInfo.seatNumber != 0) {
            performanceInstanceInfo.remainSeat--
            seatInfo.availability = false
        }

        return ticketRepository.save(ticket)
    }

    private fun performanceInstanceFindById(performanceInstanceId: Long?): PerformanceInstance {
        return performanceInstanceRepository.findByIdOrNull(performanceInstanceId)
            ?: throw ModelNotFoundException("PerformanceInstance", performanceInstanceId)
    }

    @Test
    fun 회원수확인() {
        println(memberRepository.findAll().size)
    }

    @Test
    fun 가입된회원조회() {
        val test = memberService.viewAllMemberData()

        for (i in test) {
            println(i.id)
            println(i.email)
            println(i.username)
            println()
        }
    }

    @Test
    fun 일괄회원삭제() {
        val allMemberId = memberRepository.findAll().map { it.id }
        for (m in allMemberId) {
            val findMember = memberRepository.findByIdOrNull(m)!!
            memberRepository.delete(findMember)
        }
    }

    @Test
    fun a좌석갯수확인() {
        val size = memberRepository.findAll()
        for (i in size) {
            memberRepository.delete(i)
        }
    }

    @Test
    fun 동시예매확인() {

        val allMemberInfo = memberRepository.findAll()
        val memberIdList: MutableList<Long> = mutableListOf()
        var count = allMemberInfo.size

        while (count < 4) {
            val data = Member(
                email = "temp" + count + "@naver.com",
                password = "string",
                username = "username" + count,
                role = MemberRole.TempNameConsumer
            )

            val memberEmail = memberRepository.findByEmail(data.email)
            if (memberEmail == null) {
                memberRepository.save(data)
            }
            count++
        }

        for (m in allMemberInfo) {
            memberIdList.add(m.id!!)
        }

        val memberInfo = memberRepository.findByIdOrNull(memberIdList[0])!!.id!!
        val member2Info = memberRepository.findByIdOrNull(memberIdList[1])!!.id!!
        val member3Info = memberRepository.findByIdOrNull(memberIdList[2])!!.id!!
        val member4Info = memberRepository.findByIdOrNull(memberIdList[3])!!.id!!


        val executors = Executors.newFixedThreadPool(4)

        val latch = CountDownLatch(4)

        executors.execute {
            ticketService.ticketing(memberInfo, 9290)
            latch.countDown()
        }

        executors.execute {
            ticketService.ticketing(member2Info, 8225)
            latch.countDown()
        }

        executors.execute {
            ticketService.ticketing(member3Info, 8225)
            latch.countDown()
        }

        executors.execute {
            ticketService.ticketing(member4Info, 8225)
            latch.countDown()
        }
        latch.await()  // 일괄 종료시점을 맞추는 로직?

        val myTicketInfo = memberService.viewMyAllTicket(member2Info)

        println("========================================")
        println("========================================")
        for (t in myTicketInfo) {
            println(t)
        }

        println("========================================")
        println("========================================")
    }

    @Test
    @Transactional
    fun 티켓예약변경() {

        var seatId = "1".toLong()
        var searInfo = seatInfoRepository.findByIdOrNull(seatId) ?: throw ModelNotFoundException("SeatInfo", seatId)
        val tempMember = 임시데이터_회원가입()

        while (!searInfo.availability) {
            seatId++
            searInfo = seatInfoRepository.findByIdOrNull(seatId) ?: throw ModelNotFoundException("SeatInfo", seatId)
        }

        val tempTicket = 티켓예약(tempMember.id!!, seatId)

        val result = ticketService.rescheduleTicket(tempMember.id!!, tempTicket.id!!, 10001)

        println(result)
    }


    private fun 임시데이터_공연시설(): FacilityDetail {
        val fd = FacilityDetail(
            facilityDetailName = "임시 본관",
            seatCnt = "100",
            facilityName = "임시 시설이름",
            facilityId = "TEMP_FC",
        )
        return facilityDetailRepository.save(fd)
    }

    private fun 임시데이터_시설인스턴스(fd: FacilityDetail): FacilityInstance {
        val fi = FacilityInstance(
            facilityDetail = fd,
            date = LocalDate.now(),
        )

        return facilityInstanceRepository.save(fi)
    }

    private fun 임시데이터_공연인스턴스(fi: FacilityInstance): PerformanceInstance {
        val pi = PerformanceInstance(
            performanceName = "임시 공연이름",
            performanceUniqueId = "임시 공연고유Id",
            facilityInstance = fi,
            session = "임시 본관",
            date = LocalDate.now(),
            remainSeat = 100,
        )

        return performanceInstanceRepository.save(pi)
    }

    private fun 임시데이터_좌석(fd: FacilityDetail, pi: PerformanceInstance): List<SeatInfo> {
        val seatCount = fd.seatCnt.toInt()
        val seatInfoList = mutableListOf<SeatInfo>()
        for (i: Int in 1..seatCount) {
            val si = SeatInfo(
                performanceInstance = pi,
                //좌석 번호
                seatNumber = i,
                //가격
                price = "전석무료",
                //예매 가능 여부
                availability = if (i % 10 != 0) true else false,
            )
            seatInfoList.add(seatInfoRepository.save(si))
        }
        return seatInfoList
    }

    private fun 임시데이터_회원가입(): CheckMemberResponse {
        val data = SignUpRequestDTO(
            email = "temp@naver.com",
            password = "string",
            username = "username1",
            role = "TempNameConsumer"
        )
        return memberService.signUp(data)
    }

    @Test
    @Transactional
    fun 임시데이터_모음집() {
        val fd = 임시데이터_공연시설()
        val fi = 임시데이터_시설인스턴스(fd)
        val pi = 임시데이터_공연인스턴스(fi)
        val si = 임시데이터_좌석(fd, pi)
        val ui = 임시데이터_회원가입()

//        assertTrue((fd.seatCnt == "100"), "좌석수")
//        assertTrue((fi.availability), "true")
//        assertTrue((pi.performanceName == "임시 공연이름"),"공연이름오류")
//        assertTrue(si[9].availability == false,"예매불가")
//        assertTrue(si[10].availability == true,"예매가능")
//        assertTrue((ui.email == "temp@naver.com"),"이메일 오류")

    }

    @Test
    @Transactional
    fun 좌석예매() {

        val fd = 임시데이터_공연시설()
        val fi = 임시데이터_시설인스턴스(fd)
        val pi = 임시데이터_공연인스턴스(fi)
        val si = 임시데이터_좌석(fd, pi)
        val ui = 임시데이터_회원가입()

        assertTrue(ticketService.ticketing(ui.id!!, si[9].id!!) == "예매하실수 없는 좌석입니다", "예매 불가 좌석")
        assertTrue(ticketService.ticketing(ui.id!!, si[10].id!!) == "예매 완료 되었습니다", "예매 완료 좌석")
        assertTrue(ticketService.ticketing(ui.id!!, si[10].id!!) == "예매하실수 없는 좌석입니다", "다시 예매불가 좌석")
    }

    @Test
    fun 좌석변경_완료() {

        val fd = 임시데이터_공연시설()
        val fi = 임시데이터_시설인스턴스(fd)
        val pi = 임시데이터_공연인스턴스(fi)
        val siList = 임시데이터_좌석(fd, pi)
        val ui = 임시데이터_회원가입()
        ticketService.ticketing(ui.id!!, siList[10].id!!)

        val tiList = ticketRepository.findByMemberId(ui.id!!)!!
        val ti = tiList[0]
        val si = siList[0]

        val result = ticketService.rescheduleTicket(ui.id!!, ti.id!!, si.id!!)

        assertTrue(result == "예약이 변경되었습니다","예약변경 완료")
    }

    @Test
    fun 좌석변경_다른사람티켓_변경불가() {

        val fd = 임시데이터_공연시설()
        val fi = 임시데이터_시설인스턴스(fd)
        val pi = 임시데이터_공연인스턴스(fi)
        val siList = 임시데이터_좌석(fd, pi)
        val ui = 임시데이터_회원가입()

        //타인 티켓 만들기
        val otherMemberId = "99999".toLong()
        ticketService.ticketing(otherMemberId, siList[10].id!!)
        val tiList = ticketRepository.findByMemberId(otherMemberId)!!
        val ti = tiList[0]
        val si = siList[0]

        //다른 Id로 티켓 변경하기
        val result = ticketService.rescheduleTicket(ui.id!!, ti.id!!, si.id!!)

        assertTrue(ui.id != otherMemberId ,"다른 아이디 확인")
        assertTrue(result == "잘못된 요청입니다  : 다른사람으 티켓을 변경 할 수 없습니다","타인 티켓 변경")
    }

    @Test
    fun 좌석변경_존재하지_않는_좌석(){
        val fd = 임시데이터_공연시설()
        val fi = 임시데이터_시설인스턴스(fd)
        val pi = 임시데이터_공연인스턴스(fi)
        val siList = 임시데이터_좌석(fd, pi)
        val ui = 임시데이터_회원가입()

        //티켓 만들기
        ticketService.ticketing(ui.id!!, siList[10].id!!)
        val tiList = ticketRepository.findByMemberId(ui.id!!)!!
        val ti = tiList[0]

        //없는 좌석 Id
        val nullSeatId = "999999999".toLong()
        val nullSeatInfo = seatInfoRepository.findByIdOrNull(nullSeatId)

        val result = ticketService.rescheduleTicket(ui.id!!,ti.id!!,nullSeatId)

        assertTrue(nullSeatInfo ==null,"존재하지 않는 좌석")
        assertTrue(result == "잘못된 요청입니다 : 해당 좌석이 존재하지 않습니다","없는 좌석으로 변경")

    }

    @Test
    fun 좌석변경_매진인_좌석변경(){
        val fd = 임시데이터_공연시설()
        val fi = 임시데이터_시설인스턴스(fd)
        val pi = 임시데이터_공연인스턴스(fi)
        val siList = 임시데이터_좌석(fd, pi)
        val ui = 임시데이터_회원가입()

        // 티켓 만들기
        ticketService.ticketing(ui.id!!, siList[10].id!!)
        val tiList = ticketRepository.findByMemberId(ui.id!!)!!
        val ti = tiList[0]
        val si = siList[9]

        val result = ticketService.rescheduleTicket(ui.id!!,ti.id!!,si.id!!)
        assertTrue(result == "잘못된 요청입니다 : 해당 좌석은 매진입니다","매진된 좌석으로 변경")

    }

    @Test
    fun 좌석변경_다른행사_좌석으로변경(){
        val fd = 임시데이터_공연시설()
        val fi = 임시데이터_시설인스턴스(fd)
        val pi = 임시데이터_공연인스턴스(fi)
        val siList = 임시데이터_좌석(fd, pi)
        val ui = 임시데이터_회원가입()

        //티켓 만들기
        ticketService.ticketing(ui.id!!, siList[10].id!!)
        val tiList = ticketRepository.findByMemberId(ui.id!!)!!
        val ti = tiList[0]
        val si = siList[0]

        val otherSeatId = "1".toLong()
        val originalSi = si.performanceInstance.performanceUniqueId
        val otherSi = seatInfoRepository.findByIdOrNull(otherSeatId)!!.performanceInstance.performanceUniqueId


        val result = ticketService.rescheduleTicket(ui.id!!,ti.id!!,otherSeatId)

        assertTrue(originalSi != otherSi,"동일한 좌석이 아닙니다")
        assertTrue(result == "잘못된 요청입니다 : 동일한 행사 좌석이 아닙니다","다른 행사 좌석으로 변경")

    }



}