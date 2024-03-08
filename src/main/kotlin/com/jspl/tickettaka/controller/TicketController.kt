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


    // 추후 삭제
    private val performanceInstanceRepository: PerformanceInstanceRepository,
    private val seatInfoRepository: SeatInfoRepository,
    private val ticketRepository: TicketRepository

) {


    @PostMapping("/makeSeat")
    fun makeSeat(@RequestParam performanceInstanceId :Long,@RequestParam facilityDetailId :Long) {
         ticketService.makeSeat(performanceInstanceId,facilityDetailId)
    }


    @PostMapping("/finerfinerfinder")
    fun ticketing(@AuthenticationPrincipal member:User,@RequestParam performanceInstanceId:Long):ResponseEntity<String> {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.ticketing(member,performanceInstanceId))


    }

//    @PutMapping("/{id}")
//    fun ticketingCancel(@PathVariable id:Long,@RequestBody ):ResponseEntity<Unit> {
//        TODO()
//    }

}