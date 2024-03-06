package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.reqeust.PerformanceRequest
import com.jspl.tickettaka.dto.response.AvailableDateResDto
import com.jspl.tickettaka.dto.response.FacilityDetailResDto
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.service.AdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class AdminApi(
    private val adminService: AdminService
) {

    @GetMapping("/find/concert-halls/dates")
    fun findConcertHallByDate(
        @RequestParam location1: String,
        @RequestParam location2: String,
        @RequestParam startDate: String,
        @RequestParam endDate: String): ResponseEntity<List<FacilityDetailResDto>> {
        val answer = adminService.findConcertHallByDate(location1, location2, startDate, endDate)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(answer)
    }

    @GetMapping("/find/concert-halls/names")
    fun findConcertHallByName(
        @RequestParam facilityId: String): ResponseEntity<List<AvailableDateResDto>> {
        val answer = adminService.findConcertHallByName(facilityId)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(answer)
    }
}