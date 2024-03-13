package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.reqeust.TicketRequestDTO
import com.jspl.tickettaka.model.SeatInfo
import com.jspl.tickettaka.repository.PerformanceInstanceRepository
import com.jspl.tickettaka.repository.SeatInfoRepository
import com.jspl.tickettaka.repository.TicketRepository
import com.jspl.tickettaka.service.TicketService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.instrument.Instrumentation
import kotlin.random.Random

@RestController
@RequestMapping("/api/tickets")
class TicketController(
    private val ticketService: TicketService,
) {

    //좌석 만들기
    @PostMapping("/makeSeat")
    fun makeSeat(@RequestParam performanceInstanceId :Long) {
         ticketService.makeSeat(performanceInstanceId)
    }

    //에약 가능한 좌석 전부 보기
    @GetMapping("/viewSeatInfo")
    fun viewSeatInfo(@RequestParam performanceInstanceId: Long):ResponseEntity<List<Int>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.viewAllSeatInfo(performanceInstanceId))
    }

    //예약된 좌석 전부 보기
    @GetMapping("/viewSeatResInfo")
    fun viewSeatResInfo(@RequestParam performanceInstanceId: Long):ResponseEntity<List<Int>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.viewAllSeatResInfo(performanceInstanceId))
    }


    //하나의 좌석 정보 보기
    @GetMapping("{id}")
    fun viewOneSeatInfo(@RequestParam id:Long):ResponseEntity<SeatInfo> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.viewOneSeatInfo(id))
    }



    //티켓 예약하기
    @PostMapping("/ticketing")
    fun ticketing(@AuthenticationPrincipal member:User,@RequestParam seatId:Long):ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.ticketing(member,seatId))
    }

    @GetMapping("/viewAllSeatDetailInfo")
    fun viewAllSeatDetailInfo(@RequestParam performanceInstanceId :Long) :ResponseEntity<List<String>>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.viewAllSeatDetailInfo(performanceInstanceId))
    }


    //티켓 변경하기
//    @PutMapping("/{id}")
//    fun updateTicket(@PathVariable id:Long,):ResponseEntity<String>{
//        return ResponseEntity
//            .status(HttpStatus.OK)
//            .body(ticketService.)
//    }


    //티켓 취소하기
    @DeleteMapping("/{id}")
    fun cancleTicket(@PathVariable id :Long):ResponseEntity<String >{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.cancelTicket(id))
    }

    @DeleteMapping("/{id}/test")
    fun cancleTicketTest(@PathVariable id :Long){
       ticketService.cancelTicketTest(id)
    }



}