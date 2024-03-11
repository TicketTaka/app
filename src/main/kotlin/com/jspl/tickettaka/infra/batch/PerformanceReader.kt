//package com.jspl.tickettaka.infra.batch
//
//import com.jspl.tickettaka.data.PerformanceDataCrawling
//import com.jspl.tickettaka.model.Performance
//import com.jspl.tickettaka.repository.PerformanceRepository
//import org.springframework.batch.item.ItemReader
//import org.springframework.stereotype.Component
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//
//@Component
//class PerformanceReader(
//    private val performanceDataCrawling: PerformanceDataCrawling,
//    private val performanceRepository: PerformanceRepository
//): ItemReader<Performance> {
//
//    private val startDate = LocalDate.now()
//    private val endDate = startDate.plusMonths(1)
//    override fun read(): Performance? {
//        val formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//        val formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//        val existingPerformances = performanceRepository.findAll()
//        val allPerformance01 = performanceDataCrawling.fetchData(formattedStartDate, formattedEndDate.toString(), "01")
//        val allPerformance02 = performanceDataCrawling.fetchData(formattedStartDate, formattedEndDate, "02")
//        val answerPerformances: MutableList<Performance> = mutableListOf()
//
//        if (allPerformance01 != null && allPerformance02 != null) {
//            val allPerformances = allPerformance01 + allPerformance02
//
//            existingPerformances.forEach { existingPerformance ->
//                val matchingPerformance = allPerformances.find { it.uniqueId == existingPerformance.uniqueId }
//                if (matchingPerformance != null) {
//                    existingPerformance.updateState(matchingPerformance)
//                    answerPerformances.add(existingPerformance)
//                } else {
//                    answerPerformances.add(existingPerformance)
//                }
//            }
//        }
//        return answerPerformances
//    }
//}