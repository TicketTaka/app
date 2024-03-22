package com.jspl.tickettaka.infra.batch.newPerformanceBatch

import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class NewPerformanceWriter(
    private val performanceRepository: PerformanceRepository
): ItemWriter<Performance> {
    override fun write(chunk: Chunk<out Performance>) {
        performanceRepository.saveAll(chunk)
    }
}