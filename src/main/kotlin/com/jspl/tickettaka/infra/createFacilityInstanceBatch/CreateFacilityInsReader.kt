package com.jspl.tickettaka.infra.createFacilityInstanceBatch

import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.FacilityInstance
import com.jspl.tickettaka.repository.FacilityDetailRepository
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class CreateFacilityInsReader(
    private val facilityDetailRepository: FacilityDetailRepository
): ItemReader<FacilityDetail> {

    private var currentIndex = 0
    override fun read(): FacilityDetail? {
        val allFacilityDetail = facilityDetailRepository.findAll()

        return allFacilityDetail.getOrNull(currentIndex++)
    }
}