package com.jspl.tickettaka.dto.response

import com.jspl.tickettaka.model.FacilityDetail

data class FacilityDetailResDto(
    var concertHallId: Long?,
    var concertHallName: String,
    var seatCnt: String,
    var facilityId: String,
    var facilityName: String,
) {
    companion object {
        fun fromEntities(concertHalls: List<FacilityDetail>): List<FacilityDetailResDto> {
            return concertHalls.map {
                val dto = FacilityDetailResDto(
                    it.facilityDetailId!!,
                    it.facilityDetailName,
                    it.seatCnt,
                    it.facilityId,
                    it.facilityName
                )

                dto
            }
        }
    }
}
