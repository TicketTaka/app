package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.TicketRequestDTO
import com.jspl.tickettaka.dto.response.TempPerfomanceDate
import com.jspl.tickettaka.dto.response.TicketResponse
import com.jspl.tickettaka.infra.exception.ErrorResponse
import com.jspl.tickettaka.infra.exception.ModelNotFoundException
import com.jspl.tickettaka.model.*
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
        //공연회차 정보 가져오기
        val performanceInstanceInfo = performanceInstanceFindById(performanceInstanceId)
        //공연회차에서 가지고 있는 유니크Id를 통해 공연 정보 가져오기
        val performanceInfo = performanceRepository.findByUniqueId(performanceInstanceInfo.performanceUniqueId)
        //공연회차와 연관된 공연시설에서 좌석수 가져오기
        val seatCount = performanceInstanceInfo.facilityInstance.facilityDetail.seatCnt.toInt()

        //좌석을 제작
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


    //좌석 예매하기
    fun ticketing(memberId: Long, seatId: Long): String {

        val seatInfo = seatInfoRepository.findWithLockById(seatId)
            ?: throw ModelNotFoundException("SeatInfo", seatId)

        val performanceInstanceId = seatInfo.performanceInstanceId
        val performanceInstanceInfo = performanceInstanceFindById(performanceInstanceId)


        if (!seatInfo.availability) {
            return "예매하실수 없는 좌석입니다"
        }

        val ticket = Ticket(
            memberId = memberId,                                    //나의 정보
            performanceInstanceId = seatInfo.performanceInstanceId, //공연Id
            performanceName = seatInfo.performance_name,            //공연이름
            priceInfo = seatInfo.price,                             //금액
            seatId = seatInfo.id,                                   //좌석Id
            setInfo = seatInfo.seatNumber.toString(),               //좌석번호
            reservedTime = performanceInstanceInfo.date.toString()  //예매된 시간
        )

        //엔티티내부에 넣어서 진행
        performanceInstanceInfo.remainSeat--

        seatInfo.availability = false
        ticketRepository.save(ticket)
        return "예매 완료 되었습니다"
    }

    //예매된 티켓 취소
    fun cancelTicket(ticketId: Long): String {
        val ticketInfo = ticketRepository.findByIdOrNull(ticketId)
            ?: throw ModelNotFoundException("Ticket", ticketId)
        ticketRepository.delete(ticketInfo)

        val seatInfo = seatInfoRepository.findByIdOrNull(ticketInfo.seatId)!!
        seatInfo.availability = true

        val performanceInstanceInfo = performanceInstanceFindById(seatInfo.performanceInstanceId)

        performanceInstanceInfo.remainSeat++

        return "예약을 취소 하였습니다"
    }

    //티켓 예약일 변경
    fun rescheduleTicket(memberId: Long, ticketId: Long,seatId: Long): String {
        var ticketInfo =  ticketRepository.findByIdOrNull(ticketId)!!
        val bfSeatInfo  = seatInfoRepository.findByIdOrNull(ticketInfo.seatId)!!
        val afSeatInfo = seatInfoRepository.findByIdOrNull(seatId)!!

        ticketInfo.seatId = seatId
        ticketInfo.setInfo = afSeatInfo.seatNumber.toString()
        ticketInfo.performanceInstanceId = afSeatInfo.performanceInstanceId
        bfSeatInfo.availability = true
        afSeatInfo.availability = false

        ticketRepository.save(ticketInfo)

        return "해당 날짜로 예약이 변경되었습니다 "
    }

    //같은 행사 다른 날자 확인
    fun performanceInstanceCheck(ticketId: Long):List<TempPerfomanceDate> {

        val ticketInfo = ticketRepository.findByIdOrNull(ticketId)!!
        val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(ticketInfo.performanceInstanceId)!!
        val uniqueId = performanceInstanceInfo.performanceUniqueId


        val result = mutableListOf<TempPerfomanceDate>()
        //예약 가능한 날짜 PI 가져오기
        val performanceInstanceInfoList = performanceInstanceRepository.findByPerformanceUniqueId(uniqueId)

        for(p in performanceInstanceInfoList) {
            val seatInfo = seatInfoRepository.findByPerformanceInstanceIdAndAvailability(p.performanceInstanceId!!,true)

            val date = TempPerfomanceDate(
                date = p.date,
               seatNumber = seatInfo.map { "ID ${it.id} : 좌석번호 ${it.seatNumber}"}
            )

            result.add(date)
        }


        //좌석 정보 불러오기



            return result

    }

    private fun findByPerformanceInstanceIdAndAvailability(
        performanceInstanceId: Long,
        boolean: Boolean
    ): List<SeatInfo> {
        return seatInfoRepository.findByPerformanceInstanceIdAndAvailability(performanceInstanceId, boolean)
    }

    private fun performanceInstanceFindById(performanceInstanceId: Long?): PerformanceInstance {
        return performanceInstanceRepository.findByIdOrNull(performanceInstanceId)
            ?: throw ModelNotFoundException("PerformanceInstance", performanceInstanceId)
    }

    ///////////////////////////////////[비지니스 로직 아님]/////////////////////////////////////////////////////////////////////////////////////////
    //남은 좌석 정보 보기
//    fun viewAllSeatInfo(performanceInstanceId: Long): List<Int> {
    fun viewAllSeatInfo(performanceInstanceId: Long): List<String> {
        val seatInfo = findByPerformanceInstanceIdAndAvailability(performanceInstanceId, true)
        val seatList: MutableList<String> = mutableListOf()

        for (seat in seatInfo) {
            seatList.add("ID ${seat.id} : 좌석번호 ${seat.seatNumber}")
        }

        return seatList
    }

    //예약된 좌석 정보보기
    fun viewAllSeatResInfo(performanceInstanceId: Long): List<Int> {
        val seatInfo = findByPerformanceInstanceIdAndAvailability(performanceInstanceId, false)
        val seatList: MutableList<Int> = mutableListOf()

        for (seat in seatInfo) {
            seatList.add(seat.seatNumber)
        }

        return seatList
    }


    //예약된 자리 자세히 보기
    fun viewAllSeatDetailInfo(performanceInstanceId: Long): List<String> {
        val seatInfo = findByPerformanceInstanceIdAndAvailability(performanceInstanceId, false)
        val seatList: MutableList<String> = mutableListOf()

        for (seat in seatInfo) {
            val ticketInfo = ticketRepository.findBySeatId(seat.id!!)
            seatList.add("예약자 : ${ticketInfo.memberId} 예약한 자리 :${seat.id}")
        }

        return seatList
    }

    //1개의 좌석 정보 보기
    fun viewOneSeatInfo(seatId: Long): SeatInfo {
        val seatInfo = seatInfoRepository.findByIdOrNull(seatId) ?: throw ModelNotFoundException("Seat", seatId)
        return seatInfo
    }

    //한 유저의 전체 티켓 취소
    fun cancelTicketTest(memberId: Long) {
        val ticketInfo = ticketRepository.findByMemberId(memberId)!!
        for (m in ticketInfo) {
            ticketRepository.delete(m)
            val seatInfo = seatInfoRepository.findByIdOrNull(m.seatId)!!
            seatInfo.availability = true

//            val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(seatInfo.performanceInstanceId)
//                ?: throw ModelNotFoundException("PerformanceInstance", seatInfo.performanceInstanceId)

            val performanceInstanceInfo = performanceInstanceFindById(seatInfo.performanceInstanceId)
            performanceInstanceInfo.remainSeat++
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}