package com.jspl.tickettaka.infra.batch.createPerformanceInstanceBatch

import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import jakarta.persistence.EntityManager
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class CreatePerformanceInsReader(
    private val entityManager: EntityManager
): ItemReader<Performance> {

    private var currentIndex = 0
    override fun read(): Performance? {
        val today = LocalDate.now()
        val lastDate = today.plusMonths(1)
        val query = entityManager.createNativeQuery(
            "SELECT * FROM Performance p WHERE p.start_date >= :startDate AND p.end_date < :endDate",
            Performance::class.java
        ).apply {
            setParameter("startDate", today)
            setParameter("endDate", lastDate)
        }

        val resultList = query.resultList
        return resultList.getOrNull(currentIndex++) as? Performance
    }
}