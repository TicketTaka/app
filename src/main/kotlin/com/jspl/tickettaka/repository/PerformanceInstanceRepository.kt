package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.PerformanceInstance
import org.springframework.data.jpa.repository.JpaRepository

interface PerformanceInstanceRepository:JpaRepository<PerformanceInstance, Long> {
}