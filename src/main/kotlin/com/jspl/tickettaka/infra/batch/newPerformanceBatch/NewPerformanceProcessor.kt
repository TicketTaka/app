package com.jspl.tickettaka.infra.batch.newPerformanceBatch

import com.jspl.tickettaka.data.PerformanceDataCrawling
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class NewPerformanceProcessor(
    private val performanceRepository: PerformanceRepository
): ItemProcessor<Performance, Performance> {
    override fun process(item: Performance): Performance? {
        return if(!performanceRepository.existsByUniqueId(item.uniqueId)) {
            item
        }else {
            null
        }
    }
}