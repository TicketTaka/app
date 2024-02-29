package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.PerformanceRequest
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.repository.AdminRepository
import com.jspl.tickettaka.repository.FacilityDetailRepository
import com.jspl.tickettaka.repository.FacilityRepository
import com.jspl.tickettaka.repository.PerformanceRepository
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
    private val facilityDetailRepository: FacilityDetailRepository
) {
    fun createPerformance(sido: String,
                          gugun: String,
                          startDate: String,
                          endDate: String): List<FacilityDetail> {
        val possibleFacilities: MutableList<FacilityDetail> = mutableListOf()
        val findFacilities = facilityRepository.findFacilityOfLocation(sido, gugun)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val formatStartDate = LocalDate.parse(startDate, formatter)
        val formatEndDate = LocalDate.parse(endDate, formatter)

        for(facility in findFacilities) {
            val facilityId = facility.uniqueId
            val concertHalls = facilityDetailRepository.findAllByFacilityId(facilityId)
            val performances = performanceRepository.findAllByLocationId(facilityId, formatStartDate, formatEndDate)

            var possibleFacilityCnt = concertHalls.size
            val random = Random()
            val selectedIndexes = mutableSetOf<Int>()
            if(performances.isNotEmpty()) {
                if(possibleFacilityCnt != 1) {
                    possibleFacilityCnt -= performances.size
                    while(possibleFacilityCnt != 0) {
                        var randomIndex = random.nextInt(concertHalls.size)
                        while (selectedIndexes.contains(randomIndex)) {
                            randomIndex = random.nextInt(concertHalls.size)
                        }
                        selectedIndexes.add(randomIndex)
                        possibleFacilities.add(concertHalls[randomIndex])
                        possibleFacilityCnt--
                    }
                }
            } else {
                for(concertHall in concertHalls) {
                    possibleFacilities.add(concertHall)
                }
            }
        }

        return possibleFacilities
    }

}