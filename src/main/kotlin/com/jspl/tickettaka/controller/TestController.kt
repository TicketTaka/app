package com.jspl.tickettaka.controller

import com.jspl.tickettaka.model.SeatInfo
import com.jspl.tickettaka.repository.*
import org.springframework.data.jpa.repository.Query
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


    //콘서트
    @GetMapping("/performanceRepository")
    fun test1(@RequestParam test1 :Long){
        val data = performanceRepository.findByIdOrNull(test1)!!
        println("=============performanceRepository===========")
        println(data.title)        //love supreme: 제이슨 리 1st 단독 공연
        println(data.uniqueId)      //PF236935
        println(data.location)      //벨로주 [홍대]
        println(data.locationId)      //FC002035
        println(data.startDate)
        println(data.endDate)
        println(data.genre)
        println(data.priceInfo)   //좌석 가격이 나옴
        println(data.state)        //공연상태 (공연예정)
        println(data.performanceId)
        println("========================")
    }

    //공연 회차
    @GetMapping("/performanceInstanceRepository")
    fun test2(@RequestParam test1 :Long){
        val data = performanceInstanceRepository.findByIdOrNull(test1)!!
        println("============performanceInstanceRepository============")
        println(data.date)
        println(data.performanceInstanceId)
        println(data.session)               //본관
        println(data.remainSeat)            //남은 좌석수
        println(data.performanceName)       //나의 그림일기
        println(data.facilityInstance.facilityInstanceId)
        println(data.performanceUniqueId)//PF236994
        println("========================")
    }

    //공연시설
    @GetMapping("/facilityRepository")
    fun test7(@RequestParam test1 :Long){
        val data = facilityRepository.findByIdOrNull(test1)!!
        println("=============facilityRepository===========")
        println(data.uniqueId)//FC001548
        println(data.location)
        println(data.name)
        println(data.facilityId)
        println(data.sido)
        println(data.character)
        println(data.detailCnt)   //1
        println(data.gugun)
        println(data.seatScale)   //최대 좌석수

        println("========================")
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
        println(data.seatCnt)      //좌석수
        println(data.facilityId)//FC001419
        println(data.facilityName)//1m클래식아트홀
        println(data.facilityDetailId)
        println(data.facilityDetailName)//본관
        println(data.instanceList)
        println("========================")
    }



    //공연장 인스턴스
    @GetMapping("/facilityInstanceRepository")
    fun test5(@RequestParam test1 :Long){
        val data = facilityInstanceRepository.findByIdOrNull(test1)!!
        println("=============facilityInstanceRepository===========")
        println(data.date)
        println(data.availability)   //대여가능여부
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
        println(data.performanceInstanceId)
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

}