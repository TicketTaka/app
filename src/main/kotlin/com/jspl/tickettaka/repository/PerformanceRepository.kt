package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Performance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.io.Serializable
import java.time.LocalDate
import java.util.Date

interface PerformanceRepository:JpaRepository<Performance, Long> {
    @Query("select p from Performance p where " +
            "p.startDate <= :endDate and p.endDate >= :startDate")
    fun findAllByDate(startDate: LocalDate, endDate: LocalDate): List<Performance>

    fun findByUniqueId(uniqueId :String):Performance
}