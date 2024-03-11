package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.TicketRequestDTO
import com.jspl.tickettaka.dto.response.TicketResponse
import com.jspl.tickettaka.infra.exception.ErrorResponse
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.SeatInfo
import com.jspl.tickettaka.model.Ticket
import com.jspl.tickettaka.repository.*
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class TicketService(
//    private val facilityRepository: FacilityRepository,
//    private val facilityInstanceRepository: FacilityInstanceRepository
    private val facilityDetailRepository: FacilityDetailRepository,
    private val performanceInstanceRepository: PerformanceInstanceRepository,
    private val seatInfoRepository: SeatInfoRepository,
    private val ticketRepository: TicketRepository
) {


    fun makeSeat(performanceInstanceId: Long, facilityDetailId: Long) {
        val performanceInfo = performanceInstanceRepository.findByIdOrNull(performanceInstanceId)

//        if (performanceInfo == null) {
            val facilityDetailInfo = facilityDetailRepository.findByIdOrNull(facilityDetailId)!!
            val seatCount = facilityDetailInfo.seatCnt.toInt()

            //다른 곳에서 좌석 정보
            for (i in 0..seatCount) {
                val seatInfo = SeatInfo(
                    //공연장 회자 아이디
                    performanceInstanceId = performanceInstanceId,
                    //좌석 번호
                    seatNumber = i,
                    //좌석 이름
                    seatName = "자리 이름",
                    //가격
                    price = 12345,
                    //예매 가능 여부
                    availability = true
                )

                seatInfoRepository.save(seatInfo)
            }
//        }

    }

    fun ticketing(member: User, performanceInstanceId: Long): String {

        var seatInfo = seatInfoRepository.findByPerformanceInstanceIdAndAvailability(performanceInstanceId, true)

        val ticket = Ticket(
            //회원 아이디
            memberId = member.username.toLong(),
            //공연 회차 아이디
            performanceInstanceId = performanceInstanceId,
            //금액
            priceInfo = seatInfo.price,
            //좌석정보
            setInfo = seatInfo.seatNumber.toString() + "/" + seatInfo.seatName,
            //예매된 시간
            reservedTime = "fixTime 18:00",
        )

        val seatId = seatInfo.id!!
        val seatData = seatInfoRepository.findById(seatId)

        ticketRepository.save(ticket)



        //공연장 정보 가져오기
//        val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(performanceInstanceId)!!
//
//        //공연장 자리가 이미 만들어 져 있는지 확인
//        val remainSeat = performanceInstanceInfo.remainSeat.toInt()
//        var seatInfo = seatInfoRepository.findFirstByPerformanceInstanceId(performanceInstanceId)
//
//        //공연장의 자리정보 가져오기
//        seatInfo = seatInfoRepository.findFirstByPerformanceInstanceId(performanceInstanceId)!!
//        val trueSeatInfo = seatInfoRepository.findByPerformanceInstanceIdAndAvailability(performanceInstanceId, true)
//        val seatCount = seatInfoRepository.countByPerformanceInstanceId(performanceInstanceId)
//
//        //공연장의 자리정보에서 seatNumber값을 넣기
//
//        val ticket = Ticket(
//            //회원 아이디
//            memberId = member.username.toLong(),
//            //공연 회차 아이디
//            performanceInstanceId = performanceInstanceId,
//            //금액
//            priceInfo = seatInfo.price,
//
//            //좌석정보
//            setInfo = seatInfo.seatNumber.toString() + "/" + seatInfo.seatName,
//            //예매된 시간
//            reservedTime = "fixTime 18:00",
//        )
//        //티켓을 만들기
//
//        ticketRepository.save(ticket)
        return "예매 완료 되었습니다"
    }

}