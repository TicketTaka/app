package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.response.TempFacilityResponse
import com.jspl.tickettaka.dto.response.TempPerformanceInstanceResponse
import com.jspl.tickettaka.repository.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(
    private val performanceRepository: PerformanceRepository,
    private val performanceInstanceRepository : PerformanceInstanceRepository,
    private val seatInfoRepository: SeatInfoRepository,
    private val ticketRepository: TicketRepository,
    private val facilityInstanceRepository: FacilityInstanceRepository,
    private val facilityDetailRepository: FacilityDetailRepository,
    private val facilityRepository: FacilityRepository
) {
    /*

    //콘서트
    @GetMapping("/performanceRepository")
    fun test1(@RequestParam test1 :Long){
        val data = performanceRepository.findByIdOrNull(test1)!!
        println("=============performanceRepository===========")
        println(data.title)
        println(data.uniqueId)
        println(data.location)
        println(data.locationId)
        println(data.startDate)
        println(data.endDate)
        println(data.genre)
        println(data.priceInfo)
        println(data.state)
        println(data.performanceId)
        println("========================")
    }

    //공연 회차
    @GetMapping("/performanceInstanceRepository")
    fun test2(@RequestParam test1 :Long) :TempPerformanceInstanceResponse {
        val data = performanceInstanceRepository.findByIdOrNull(test1)!!
        val data2 :MutableIterable<String> = mutableListOf()
        println("============performanceInstanceRepository============")
        println(data.date)
        println(data.performanceInstanceId)
        println(data.session)
        println(data.remainSeat)
        println(data.performanceName)
        println(data.facilityInstance.facilityDetail.seatCnt)
        println(data.performanceUniqueId)

        val result = TempPerformanceInstanceResponse(
            data = data.date.toString(),
            performanceInstanceId =data.performanceInstanceId.toString(),
            performanceName = data.performanceName,
            remainSeat = data.remainSeat.toString(),
            seatCnt = data.facilityInstance.facilityDetail.seatCnt
        )

        println("========================")

        return result
    }

    //공연시설
    @GetMapping("/facilityRepository")
    fun test7(@RequestParam test1 :Long):TempFacilityResponse{
        val data = facilityRepository.findByIdOrNull(test1)!!
        println("=============facilityRepository===========")
        val data2: MutableList<String> = mutableListOf()

        data2.add(data.uniqueId)
        data2.add(data.facilityId.toString())
        data2.add(data.name)
        data2.add(data.detailCnt)
        data2.add(data.seatScale)
        println(data.uniqueId)
        println(data.location)
        println(data.name)
        println(data.facilityId)
        println(data.sido)
        println(data.character)
        println(data.detailCnt)
        println(data.gugun)
        println(data.seatScale)

        println("========================")
        val data3 = TempFacilityResponse(
            uniqueId = data.uniqueId,
            facilityId = data.facilityId.toString(),
            name = data.name,
            detailCnt = data.detailCnt,
            seatScale = data.seatScale
        )
        return data3
    }

    @GetMapping("/reqpar")
    fun test8(@RequestParam test1 :Long){
        println(test1)
        println("========================")
    }


    @GetMapping("/facilityDetailRepository")
    fun test6(@RequestParam test1 :Long){
        val data = facilityDetailRepository.findByIdOrNull(test1)!!
        println("=============facilityDetailRepository===========")
        println(data.seatCnt)
        println(data.facilityId)
        println(data.facilityName)
        println(data.facilityDetailId)
        println(data.facilityDetailName)
        println(data.instanceList)
        println("========================")
    }

    //공연장 인스턴스
    @GetMapping("/facilityInstanceRepository")
    fun test5(@RequestParam test1 :Long){
        val data = facilityInstanceRepository.findByIdOrNull(test1)!!
        println("=============facilityInstanceRepository===========")
        println(data.date)
        println(data.availability)
        println(data.facilityDetail)
        println(data.facilityInstanceId)
        println(data.performanceInstanceList)
        println("========================")
    }

    @GetMapping("/ticketRepository")
    fun test4(@RequestParam test1 :Long){
        val data = ticketRepository.findByIdOrNull(test1)!!
        println("=============ticketRepository===========")
        println(data.id)
        println(data.setInfo)
        println(data.memberId)
        println(data.reservedTime)
        println(data.priceInfo)
        println("========================")
    }

    @GetMapping("/seatInfoRepository")
    fun test3(@RequestParam test1 :Long){
        val data = seatInfoRepository.findByIdOrNull(test1)!!
        println("============seatInfoRepository============")
        println(data.id)
        println(data.price)
        println(data.availability)
        println(data.performanceInstanceId)
        println(data.seatName)
        println("========================")
    }

     */

}