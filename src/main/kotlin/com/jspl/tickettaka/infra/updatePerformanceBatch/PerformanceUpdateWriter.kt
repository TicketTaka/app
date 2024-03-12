package com.jspl.tickettaka.infra.updatePerformanceBatch

import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class PerformanceUpdateWriter(
    private val performanceRepository: PerformanceRepository
): ItemWriter<Performance> {
    override fun write(chunk: Chunk<out Performance>) {
        performanceRepository.saveAll(chunk)
    }
}