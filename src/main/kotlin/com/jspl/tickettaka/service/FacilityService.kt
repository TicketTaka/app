package com.jspl.tickettaka.service

import com.jspl.tickettaka.data.FacilityDataCrawling
import com.jspl.tickettaka.repository.FacilityRepository
import org.springframework.stereotype.Service

@Service
class FacilityService(
    private val facilityDataCrawling: FacilityDataCrawling,
    private val facilityRepository: FacilityRepository
) {
    fun facilityUpdate() {
        facilityDataCrawling.fetchAndSaveFacilities()
    }

    fun facilityDetailUpdate() {
        facilityDataCrawling.createConcertHall()
    }

    fun findLocation1(): List<String> {
        return facilityRepository.findLocation1()
    }

    fun findLocation2(sido: String): List<String> {
        return facilityRepository.findLocation2(sido)
    }
}