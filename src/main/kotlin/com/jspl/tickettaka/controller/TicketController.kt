package com.jspl.tickettaka.controller


import com.jspl.tickettaka.dto.response.SeatInfoResDto
import com.jspl.tickettaka.dto.response.TempPerfomanceDate
import com.jspl.tickettaka.service.TicketService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tickets")
class TicketController(
    private val ticketService: TicketService,
) {

    //에약 가능한 좌석 전부 보기
    @GetMapping("/viewSeatInfo")
    fun viewSeatInfo(@RequestParam performanceInstanceId: Long): ResponseEntity<List<SeatInfoResDto>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.viewAllSeatInfo(performanceInstanceId))
    }

    //예약된 좌석 전부 보기
    @GetMapping("/viewSeatResInfo")
    fun viewSeatResInfo(@RequestParam performanceInstanceId: Long): ResponseEntity<List<Int>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.viewAllSeatResInfo(performanceInstanceId))
    }

    //티켓 예약하기
    @PostMapping("/ticketing")
    fun ticketing(@AuthenticationPrincipal member: User, @RequestParam seatId: Long): ResponseEntity<String> {
        val memberId = member.username.toLong()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.ticketing(memberId, seatId))
    }

    //티켓 변경하기
    @PutMapping("/{ticketId}")
    fun updateTicket(
        @AuthenticationPrincipal member: User,
        @PathVariable ticketId: Long,
        @RequestParam seatId: Long
    ): ResponseEntity<String> {
        val memberId = member.username.toLong()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.rescheduleTicket(memberId, ticketId, seatId))
    }

    //티켓 취소하기
    @DeleteMapping("/{id}")
    fun cancleTicket(@PathVariable id: Long): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.cancelTicket(id))
    }

    //같은 행사 다른 날짜 확인하기
    @GetMapping("/checkPerformance")
    fun performanceInstanceCheck(@RequestParam id: Long): ResponseEntity<List<TempPerfomanceDate>> {
        return ResponseEntity.status(HttpStatus.OK).body(ticketService.performanceInstanceCheck(id))
    }

    //좌석 만들기
    @PostMapping("/makeSeat")
    fun makeSeat() {
        ticketService.makeSeat()
    }

    ////////////////////////////////////[비지니스 로직 아님]///////////////////////////////////////////////
    /*
    //예약된 좌석상세 확인
    @GetMapping("/viewAllSeatDetailInfo")
    fun viewAllSeatDetailInfo(@RequestParam performanceInstanceId: Long): ResponseEntity<List<String>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ticketService.viewAllSeatDetailInfo(performanceInstanceId))
    }

    //한 유저의 모든 티켓 취소하기
    @DeleteMapping("/{id}/test")
    fun cancleTicketTest(@PathVariable id: Long) {
        ticketService.cancelTicketTest(id)
    }

     */
}