package com.jspl.tickettaka.controller

import com.jspl.tickettaka.service.TicketService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tickets")
class TicketController(
    private val ticketService: TicketService
) {

    @GetMapping
    fun ticketing():ResponseEntity<String> {

        TODO()
    }

    @PutMapping("/{id}")
    fun ticketingCancel(@PathVariable id:Long):ResponseEntity<Unit> {
        TODO()
    }

}