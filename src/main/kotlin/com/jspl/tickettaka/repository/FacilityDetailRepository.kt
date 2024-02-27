package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.FacilityDetail
import org.springframework.data.jpa.repository.JpaRepository

interface FacilityDetailRepository:JpaRepository<FacilityDetail, Long> {
}