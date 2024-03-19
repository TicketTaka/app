package com.jspl.tickettaka.service

import com.jspl.tickettaka.data.PerformanceDataCrawling
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import org.springframework.stereotype.Service

@Service
class PerformanceService(
    private val performanceDataCrawling: PerformanceDataCrawling,
) {
    fun updatePerformance() {
        performanceDataCrawling.execute("20240301", "20240401")

    }

    fun updateInstance() {
        performanceDataCrawling.createInstance()
    }
}