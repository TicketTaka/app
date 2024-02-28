package com.jspl.tickettaka.service

import com.jspl.tickettaka.data.FacilityDataCrawling
import org.springframework.stereotype.Service

@Service
class FacilityService(
    private val facilityDataCrawling: FacilityDataCrawling
) {
    fun facilityUpdate() {
        facilityDataCrawling.fetchAndSaveFacilities()
    }

    fun facilityDetailUpdate() {
        facilityDataCrawling.createConcertHall()
    }
}