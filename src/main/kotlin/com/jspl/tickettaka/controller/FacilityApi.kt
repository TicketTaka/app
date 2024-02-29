package com.jspl.tickettaka.controller

import com.jspl.tickettaka.service.FacilityService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class FacilityApi(
    private val facilityService: FacilityService
) {
    @GetMapping("/update-facility")
    fun updateFacility() {
        facilityService.facilityUpdate()
    }

    @GetMapping("update-facility-details")
    fun updateFacilityDetail() {
        facilityService.facilityDetailUpdate()
    }

    @GetMapping("api/sido")
    fun findLocation1(): List<String> {
        return facilityService.findLocation1()
    }

    @GetMapping("api/gugun")
    fun findLocation2(@RequestParam sido: String): List<String> {
        return facilityService.findLocation2(sido)
    }
}