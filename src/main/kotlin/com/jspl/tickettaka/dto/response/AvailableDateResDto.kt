package com.jspl.tickettaka.dto.response

import com.jspl.tickettaka.model.FacilityInstance
import java.time.LocalDate

data class AvailableDateResDto(
    var facilityDetailName: String,
    var date: LocalDate
) {
    companion object {
        fun fromEntities(facilityInstances: List<FacilityInstance>): List<AvailableDateResDto> {
            return facilityInstances.map {
                val dto = AvailableDateResDto(
                    it.facilityDetail.facilityDetailName,
                    it.date
                )

                dto
            }
        }
    }
}
