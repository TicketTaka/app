package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.response.SeatInfoResDto
import com.jspl.tickettaka.dto.response.TempPerfomanceDate
import com.jspl.tickettaka.infra.exception.ModelNotFoundException
import com.jspl.tickettaka.model.*
import com.jspl.tickettaka.repository.*
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Transactional
class TicketService(
    private val performanceInstanceRepository: PerformanceInstanceRepository,
    private val performanceRepository: PerformanceRepository,
    private val seatInfoRepository: SeatInfoRepository,
    private val ticketRepository: TicketRepository
) {

    //좌석 만들기

    fun makeSeat() {
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(12)

        val formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val parsedStartDate = LocalDate.parse(formattedStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val parsedEndDate = LocalDate.parse(formattedEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        //공연회차 전체 가져오기
        val performanceInstanceAllInfo = performanceInstanceRepository.findPerformanceInstanceInOneWeek(parsedStartDate, parsedEndDate)

        //for문으로 하나하나 공연회차 값 가져오기
        for (p in performanceInstanceAllInfo) {

            //공연회차에서 가지고 있는 유니크Id를 통해 공연 정보 가져오기
            val performanceInfo = performanceRepository.findByUniqueId(p.performanceUniqueId)
            //공연회차와 연관된 공연시설에서 좌석수 가져오기
            val seatCount = p.facilityInstance.facilityDetail.seatCnt.toInt()


            if(seatCount == 0) {
                val seatInfo = SeatInfo(
                    performanceInstance = p,
                    //좌석 번호
                    seatNumber = 0,
                    //가격
                    price = performanceInfo.priceInfo,
                    //예매 가능 여부
                    availability = true,
                )
                seatInfoRepository.save(seatInfo)
            } else if(seatCount <= 3500) {
                val seatInfoList = mutableListOf<SeatInfo>()
                for (i in 1..seatCount) {
                    val seatInfo = SeatInfo(
                        performanceInstance = p,
                        //좌석 번호
                        seatNumber = i,
                        //가격
                        price = performanceInfo.priceInfo ?: "전석무료",
                        //예매 가능 여부
                        availability = true,
                    )
                    seatInfoList.add(seatInfo)
                }
                seatInfoRepository.saveAll(seatInfoList)
            }
        }
    }

    //좌석 예매하기
    fun ticketing(memberId: Long, seatId: Long): String {

        val seatInfo = seatInfoRepository.findWithLockById(seatId)
            ?: throw ModelNotFoundException("SeatInfo", seatId)

        val performanceInstanceId = seatInfo.performanceInstance.performanceInstanceId
        val performanceInstanceInfo = performanceInstanceFindById(performanceInstanceId)


        if (!seatInfo.availability) {
            return "예매하실수 없는 좌석입니다"
        }

        val ticket = Ticket(
            memberId = memberId,                                                     //나의 정보
            performanceInstanceId = performanceInstanceId,                           //공연Id
            performanceName = seatInfo.performanceInstance.performanceName,          //공연이름
            priceInfo = if(seatInfo.price != "") seatInfo.price else "전석무료",                                          //금액
            seatId = seatInfo.id,                                                    //좌석Id
            setInfo = seatInfo.seatNumber.toString(),                                //좌석번호
            reservedTime = seatInfo.performanceInstance.date.toString()              //예매된 시간
        )


        if(seatInfo.seatNumber != 0){
            performanceInstanceInfo.remainSeat--
            seatInfo.availability = false
        }

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

        val performanceInstanceInfo = performanceInstanceFindById(seatInfo.performanceInstance.performanceInstanceId)

        performanceInstanceInfo.remainSeat++

        return "예약을 취소 하였습니다"
    }

    //티켓 예약일 변경
    fun rescheduleTicket(memberId: Long, ticketId: Long, seatId: Long): String {
        var ticketInfo = ticketRepository.findByIdOrNull(ticketId)!!
        val bfSeatInfo = seatInfoRepository.findByIdOrNull(ticketInfo.seatId)!!
        val afSeatInfo = seatInfoRepository.findByIdOrNull(seatId)!!

        ticketInfo.seatId = seatId
        ticketInfo.setInfo = afSeatInfo.seatNumber.toString()
        ticketInfo.performanceInstanceId = afSeatInfo.performanceInstance.performanceInstanceId
        bfSeatInfo.availability = true
        afSeatInfo.availability = false

        ticketRepository.save(ticketInfo)

        return "해당 날짜로 예약이 변경되었습니다 "
    }

    //같은 행사 다른 날자 확인
    fun performanceInstanceCheck(ticketId: Long): List<TempPerfomanceDate> {

        val ticketInfo = ticketRepository.findByIdOrNull(ticketId)!!
        val performanceInstanceInfo = performanceInstanceRepository.findByIdOrNull(ticketInfo.performanceInstanceId)!!
        val uniqueId = performanceInstanceInfo.performanceUniqueId


        val result = mutableListOf<TempPerfomanceDate>()
        //예약 가능한 날짜 PI 가져오기
        val performanceInstanceInfoList = performanceInstanceRepository.findByPerformanceUniqueId(uniqueId)

        for (p in performanceInstanceInfoList) {
            val seatInfo =
                seatInfoRepository.findByPerformanceInstanceAndAvailability(p, true)

            val date = TempPerfomanceDate(
                date = p.date,
                seatNumber = seatInfo.map { "ID ${it.id} : 좌석번호 ${it.seatNumber}" }
            )

            result.add(date)
        }

        return result

    }

    private fun findByPerformanceInstanceIdAndAvailability(
        performanceInstance: PerformanceInstance,
        availability: Boolean
    ): List<SeatInfo> {
        return seatInfoRepository.findByPerformanceInstanceAndAvailability(performanceInstance, availability)
    }

    private fun performanceInstanceFindById(performanceInstanceId: Long?): PerformanceInstance {
        return performanceInstanceRepository.findByIdOrNull(performanceInstanceId)
            ?: throw ModelNotFoundException("PerformanceInstance", performanceInstanceId)
    }

    ///////////////////////////////////[비지니스 로직 아님]/////////////////////////////////////////////////////////////////////////////////////////
    //남은 좌석 정보 보기
    fun viewAllSeatInfo(performanceInstanceId: Long): List<SeatInfoResDto> {
        val performanceInstance =performanceInstanceRepository.findByIdOrNull(performanceInstanceId)!!
        var seatInfo = findByPerformanceInstanceIdAndAvailability(performanceInstance, true)
        return SeatInfoResDto.fromEntities(seatInfo)
    }

    //예약된 좌석 정보보기
    fun viewAllSeatResInfo(performanceInstanceId: Long): List<Int> {
        val performanceInstance = performanceInstanceRepository.findByIdOrNull(performanceInstanceId)!!
        val seatInfo = findByPerformanceInstanceIdAndAvailability(performanceInstance, false)
        val seatList: MutableList<Int> = mutableListOf()

        for (seat in seatInfo) {
            seatList.add(seat.seatNumber)
        }

        return seatList
    }


    //예약된 자리 자세히 보기
    fun viewAllSeatDetailInfo(performanceInstanceId: Long): List<String> {

        val performanceInstance= performanceInstanceRepository.findByIdOrNull(performanceInstanceId)!!
        val seatInfo = findByPerformanceInstanceIdAndAvailability(performanceInstance, false)
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


            val performanceInstanceInfo = performanceInstanceFindById(seatInfo.performanceInstance.performanceInstanceId)
            performanceInstanceInfo.remainSeat++
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}