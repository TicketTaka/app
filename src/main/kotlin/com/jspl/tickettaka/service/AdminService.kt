package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.PerformanceRequest
import com.jspl.tickettaka.dto.response.FacilityDetailResDto
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.repository.*
import org.springframework.stereotype.Service
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AdminService(
    private val adminRepository: AdminRepository,
    private val facilityRepository: FacilityRepository,
    private val performanceRepository: PerformanceRepository,
    private val facilityDetailRepository: FacilityDetailRepository,
    private val facilityInstanceRepository: FacilityInstanceRepository
) {
    fun findConcertHallByDate(sido: String,
                          gugun: String,
                          startDate: String,
                          endDate: String): List<FacilityDetailResDto> {
        val possibleFacilities: MutableList<FacilityDetail> = mutableListOf()
        val findFacilities = facilityRepository.findFacilityOfLocation(sido, gugun)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatStartDate = LocalDate.parse(startDate, formatter)
        val formatEndDate = LocalDate.parse(endDate, formatter)

        for(facility in findFacilities) {
            val facilityId = facility.uniqueId
            val concertHalls = facilityDetailRepository.findAllByFacilityId(facilityId)
            for(facilityDetail in concertHalls) {
                val facilityInstances = facilityInstanceRepository.findAvailableConcertHall(formatStartDate, formatEndDate, facilityDetail)

                for(facilityInstance in facilityInstances) {
                    possibleFacilities.add(facilityInstance.facilityDetail)
                }
            }
        }

        return FacilityDetailResDto.fromEntities(possibleFacilities)
    }

}