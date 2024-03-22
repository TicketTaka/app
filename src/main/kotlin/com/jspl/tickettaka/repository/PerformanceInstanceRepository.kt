package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.model.PerformanceInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface PerformanceInstanceRepository:JpaRepository<PerformanceInstance, Long> {

    fun findByPerformanceUniqueId(uniqueId :String):List<PerformanceInstance>

    @Query("select pi from PerformanceInstance pi where pi.date >= :startDate and pi.date <= :endDate")
    fun findPerformanceInstanceInOneWeek(startDate: LocalDate, endDate: LocalDate): List<PerformanceInstance>
}