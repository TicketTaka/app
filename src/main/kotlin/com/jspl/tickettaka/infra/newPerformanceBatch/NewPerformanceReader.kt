package com.jspl.tickettaka.infra.newPerformanceBatch

import com.jspl.tickettaka.data.PerformanceDataCrawling
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class NewPerformanceReader(
    private val performanceDataCrawling: PerformanceDataCrawling,
    private val performanceRepository: PerformanceRepository
): ItemReader<Performance> {

    private val startDate = LocalDate.now()
    private val endDate = startDate.plusMonths(1)
    private var currentIndex = 0
    override fun read(): Performance? {
        val formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val performance01 = performanceDataCrawling.fetchData(formattedStartDate, formattedEndDate, "01")
        val performance02 = performanceDataCrawling.fetchData(formattedStartDate, formattedEndDate, "02")

        val allPerformances = performance01!! + performance02!!
        val answerPerformances: MutableList<Performance> = mutableListOf()

        for(performance in allPerformances) {
            if(!performanceRepository.existsByUniqueId(performance.uniqueId))
                answerPerformances.add(performance)
        }

        return allPerformances.getOrNull(currentIndex++)

    }
}