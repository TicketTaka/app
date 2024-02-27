package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Performance
import org.springframework.data.jpa.repository.JpaRepository

interface PerformanceRepository:JpaRepository<Performance, Long> {
}