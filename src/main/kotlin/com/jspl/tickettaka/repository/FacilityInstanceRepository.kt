package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.FacilityInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FacilityInstanceRepository: JpaRepository<FacilityInstance, Long> {
    @Query("select fi from FacilityInstance fi where fi.facilityDetail.facilityId = :facilityName and fi.availability = true")
    fun findFacilityInstanceByFacilityName(facilityName: String): List<FacilityInstance>
}