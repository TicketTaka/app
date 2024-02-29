package com.jspl.tickettaka.controller

import com.jspl.tickettaka.dto.reqeust.PerformanceRequest
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.service.AdminService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AdminApi(
    private val adminService: AdminService
) {

    @PostMapping("/concerts")
    fun createPerformance(
        @RequestParam location1: String,
        @RequestParam location2: String,
        @RequestParam startDate: String,
        @RequestParam endDate: String): List<FacilityDetail> {
        return adminService.createPerformance(location1, location2, startDate, endDate)
    }
}