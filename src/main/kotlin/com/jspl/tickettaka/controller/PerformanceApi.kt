package com.jspl.tickettaka.controller

import com.jspl.tickettaka.service.PerformanceService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class PerformanceApi(
    private val performanceService: PerformanceService
) {
    @GetMapping("/update-performance")
    fun update() {
        performanceService.updatePerformance()
        println("complete")
    }
}