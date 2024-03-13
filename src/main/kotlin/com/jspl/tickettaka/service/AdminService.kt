package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.PerformanceRequestDTO
import com.jspl.tickettaka.dto.response.AvailableDateResDto
import com.jspl.tickettaka.dto.response.FacilityDetailResDto
import com.jspl.tickettaka.infra.exception.NotFoundException
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.FacilityInstance
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.*
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AdminService(
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

    fun findConcertHallByName(facilityId: String): List<AvailableDateResDto> {
        val facilities = facilityDetailRepository.findAllByFacilityId(facilityId)
        val possibleFacilities: MutableList<FacilityInstance> = mutableListOf()

        for(facilityDetail in facilities) {
            val facilityInstances = facilityInstanceRepository.findAvailableDate(facilityDetail)
            possibleFacilities.addAll(facilityInstances)
        }

        return AvailableDateResDto.fromEntities(possibleFacilities)
    }

    fun createPerformance(request: PerformanceRequestDTO,
                          id: Long,
                          startDate: String,
                          endDate: String) {
        val random = Random()
        var possible = false
        var checkId: String
        var uniqueId = ""
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatStartDate = LocalDate.parse(startDate, formatter)
        val formatEndDate = LocalDate.parse(endDate, formatter)

        while (!possible) {
            val alphabet = ('A'..'Z').toList()
            val firstTwoChars = (1..2)
                .map { alphabet[random.nextInt(alphabet.size)] }
                .joinToString("")

            val numbers = (0..9).toList()
            val nextSixDigits = (1..6)
                .map { numbers[random.nextInt(numbers.size)] }
                .joinToString("")

            checkId = firstTwoChars + nextSixDigits
            if(!performanceRepository.existsByUniqueId(checkId)) {
                uniqueId = checkId
                possible = true
            }
        }
        val facilityInstance = facilityInstanceRepository.findById(id).orElseThrow {
            NotFoundException(
                "공연장이 존재하지 않습니다."
            )
        }
        val facilityDetail = facilityInstance.facilityDetail
        val facility = facilityRepository.findByUniqueId(facilityDetail.facilityId)

        val performance = Performance(
            request.title,
            uniqueId,
            facility.location,
            facilityDetail.facilityId,
            formatStartDate,
            formatEndDate,
            request.genre,
            request.priceInfo,
            "공연예정"
        )

        performanceRepository.save(performance)
    }
}