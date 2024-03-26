package com.jspl.tickettaka.infra.batch.createPerformanceInstanceBatch

import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import jakarta.persistence.EntityManager
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class CreatePerformanceInsReader(
    private val performanceRepository: PerformanceRepository
): ItemReader<Performance> {

    private var currentIndex = 0
    override fun read(): Performance? {
        val today = LocalDate.now()
        val lastDate = today.plusMonths(1)
        val allPerformance = performanceRepository.findAllByDate(today, lastDate)

        return allPerformance.getOrNull(currentIndex++)
    }
}