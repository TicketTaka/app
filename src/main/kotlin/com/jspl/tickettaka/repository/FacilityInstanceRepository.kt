package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.FacilityInstance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface FacilityInstanceRepository: JpaRepository<FacilityInstance, Long> {
    @Query("select fi from FacilityInstance fi where fi.facilityDetail = :facilityDetail and fi.date = :date and fi.availability = true")
    fun findFacilityInstanceByFacilityDetailWithDate(facilityDetail: FacilityDetail, date: LocalDate): FacilityInstance?

    @Query("select fi from FacilityInstance fi where fi.date >= :startDate and fi.date <= :endDate and fi.facilityDetail = :facilityDetail and fi.availability = true")
    fun findAvailableConcertHall(startDate: LocalDate, endDate: LocalDate, facilityDetail: FacilityDetail): List<FacilityInstance>

    @Query("select fi from FacilityInstance fi where fi.facilityDetail = :facilityDetail and fi.availability = true")
    fun findAvailableDate(facilityDetail: FacilityDetail): List<FacilityInstance>

}