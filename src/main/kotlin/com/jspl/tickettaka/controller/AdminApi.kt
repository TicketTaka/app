package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.reqeust.PerformanceRequest
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

    @GetMapping("/concerts")
    fun createPerformance(
        @RequestParam location1: String,
        @RequestParam location2: String,
        @RequestParam startDate: String,
        @RequestParam endDate: String): ResponseEntity<List<FacilityDetailResDto>> {
        val answer = adminService.findConcertHall(location1, location2, startDate, endDate)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(answer)
    }
}