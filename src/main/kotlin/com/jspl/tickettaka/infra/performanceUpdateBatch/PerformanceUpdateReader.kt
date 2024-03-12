package com.jspl.tickettaka.infra.performanceUpdateBatch

import com.jspl.tickettaka.data.PerformanceDataCrawling
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PerformanceUpdateReader(
    private val performanceRepository: PerformanceRepository
): ItemReader<Performance> {

    private val startDate = LocalDate.now()
    private var currentIndex = 0
    override fun read(): Performance? {
        val existingPerformances = performanceRepository.findPerformancesByState(startDate) ?: return null

        return existingPerformances.getOrNull(currentIndex++)

    }
}