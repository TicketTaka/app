package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.FacilityDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FacilityDetailRepository:JpaRepository<FacilityDetail, Long> {
    fun findAllByFacilityId(facilityId: String): List<FacilityDetail>
//    @Query("SELECT (SELECT fd FROM FacilityDetail fd WHERE fd.facilityId = f.facilityId) FROM FacilityDetail f WHERE f.facilityId IN :facilityIds")
    fun findAllByFacilityIdIn(facilityIds: List<String>): List<FacilityDetail>
}