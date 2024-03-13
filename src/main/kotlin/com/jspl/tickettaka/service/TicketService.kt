package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.TicketRequestDTO
import com.jspl.tickettaka.dto.response.TicketResponse
import com.jspl.tickettaka.infra.exception.ErrorResponse
import com.jspl.tickettaka.infra.exception.ModelNotFoundException
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.SeatInfo
import com.jspl.tickettaka.model.Ticket
import com.jspl.tickettaka.repository.*
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
@Transactional
class TicketService(
    private val performanceInstanceRepository: PerformanceInstanceRepository,
    private val performanceRepository: PerformanceRepository,
    private val seatInfoRepository: SeatInfoRepository,
    private val ticketRepository: TicketRepository
) {

    fun makeSeat(performanceInstanceId: Long) {
        val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(performanceInstanceId)
            ?: throw ModelNotFoundException("PerformanceInstance", performanceInstanceId)
        val performanceInfo = performanceRepository.findByUniqueId(performanceInstanceInfo.performanceUniqueId)

        val seatCount = performanceInstanceInfo.facilityInstance.facilityDetail.seatCnt.toInt()

        //다른 곳에서 좌석 정보
        for (i in 1..seatCount) {
            val seatInfo = SeatInfo(
                //콘서트 이름
                performance_name = performanceInstanceInfo.performanceName,
                //콘서트 정보(고유번호)
                uniqueId = performanceInstanceInfo.performanceUniqueId,
                //공연장 회자 아이디
                performanceInstanceId = performanceInstanceInfo.performanceInstanceId,
                //좌석 번호
                seatNumber = i,
                //가격
                price = performanceInfo.priceInfo,
                //예매 가능 여부
                availability = true,
            )
            seatInfoRepository.save(seatInfo)
        }
    }

    //남은 좌석 정보 보기
    fun viewAllSeatInfo(performanceInstanceId: Long): List<Int> {
        val seatInfo = seatInfoRepository.findByPerformanceInstanceIdAndAvailability(performanceInstanceId, true)
        val seatList: MutableList<Int> = mutableListOf()

        for (seat in seatInfo) {
            seatList.add(seat.seatNumber)
        }

        return seatList
    }

    //예약된 좌석 정보보기
    fun viewAllSeatResInfo(performanceInstanceId: Long): List<Int> {
        val seatInfo = seatInfoRepository.findByPerformanceInstanceIdAndAvailability(performanceInstanceId, false)
        val seatList: MutableList<Int> = mutableListOf()

        for (seat in seatInfo) {
            seatList.add(seat.seatNumber)
        }

        return seatList
    }


    //예약된 자리 자세히 보기
    fun viewAllSeatDetailInfo(performanceInstanceId: Long): List<String> {
        val seatInfo = seatInfoRepository.findByPerformanceInstanceIdAndAvailability(performanceInstanceId, false)
        val seatList: MutableList<String> = mutableListOf()

        for (seat in seatInfo) {
            val ticketInfo = ticketRepository.findBySeatId(seat.id!!)
            seatList.add("예약자 : ${ticketInfo.memberId} 예약한 자리 :${seat.id}")
        }

        return seatList
    }

    fun viewOneSeatInfo(id: Long): SeatInfo {
        val seatInfo = seatInfoRepository.findByIdOrNull(id) ?: throw ModelNotFoundException("Seat", id)
        return seatInfo
    }

    //좌석 예매하기
    fun ticketing(member: User, seatId: Long): String {

        val seatInfo = seatInfoRepository.findByIdOrNull(seatId)
            ?: throw ModelNotFoundException("SeatInfo", seatId)

        val performanceInstanceId = seatInfo.performanceInstanceId
        val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(performanceInstanceId)
            ?: throw ModelNotFoundException("PerformanceInstance", performanceInstanceId)

        if (!seatInfo.availability) {
            return "예매하실수 없는 좌석입니다"
        }

        val ticket = Ticket(
            //나의 정보
            memberId = member.username.toLong(),
            //공연Id
            performanceInstanceId = seatInfo.performanceInstanceId,
            //공연이름
            performanceName = seatInfo.performance_name,
            //금액
            priceInfo = seatInfo.price,
            //좌석Id
            seatId = seatInfo.id,
            //좌석번호
            setInfo = seatInfo.seatNumber.toString(),
            //예매된 시간
            reservedTime = performanceInstanceInfo.date.toString()
        )

        //엔티티내부에 넣어서 진행
        performanceInstanceInfo.remainSeat--

        seatInfo.availability = false
        ticketRepository.save(ticket)
        return "예매 완료 되었습니다"
    }


    fun ticketingTest(memberId: Long, seatId: Long): String {

        val seatInfo = seatInfoRepository.findWithLockById(seatId)
            ?: throw ModelNotFoundException("SeatInfo", seatId)

        val performanceInstanceId = seatInfo.performanceInstanceId
        val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(performanceInstanceId)
            ?: throw ModelNotFoundException("PerformanceInstance", performanceInstanceId)

        if (!seatInfo.availability) {
            return "예매하실수 없는 좌석입니다"
        }

        val ticket = Ticket(
            //나의 정보
            memberId = memberId,
            //공연Id
            performanceInstanceId = seatInfo.performanceInstanceId,
            //공연이름
            performanceName = seatInfo.performance_name,
            //금액
            priceInfo = seatInfo.price,
            //좌석Id
            seatId = seatInfo.id,
            //좌석번호
            setInfo = seatInfo.seatNumber.toString(),
            //예매된 시간
            reservedTime = performanceInstanceInfo.date.toString()
        )

        //엔티티내부에 넣어서 진행
        performanceInstanceInfo.remainSeat--

        seatInfo.availability = false
        ticketRepository.save(ticket)
        return "예매 완료 되었습니다"
    }


    fun cancelTicket(ticketId: Long): String {
        val ticketInfo = ticketRepository.findByIdOrNull(ticketId)
            ?: throw ModelNotFoundException("Ticket", ticketId)
        ticketRepository.delete(ticketInfo)

        val seatInfo = seatInfoRepository.findByIdOrNull(ticketInfo.seatId)!!
        seatInfo.availability = true

        val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(seatInfo.performanceInstanceId)
            ?: throw ModelNotFoundException("PerformanceInstance", seatInfo.performanceInstanceId)
        performanceInstanceInfo.remainSeat++

        return "예약을 취소 하였습니다"
    }


    //한 유저의 전체 티켓 취소
    fun cancelTicketTest(memberId: Long) {
        val ticketInfo = ticketRepository.findByMemberId(memberId)!!
        for (m in ticketInfo) {
            ticketRepository.delete(m)
            val seatInfo = seatInfoRepository.findByIdOrNull(m.seatId)!!
            seatInfo.availability = true

            val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(seatInfo.performanceInstanceId)
                ?: throw ModelNotFoundException("PerformanceInstance", seatInfo.performanceInstanceId)
            performanceInstanceInfo.remainSeat++
        }
    }


}