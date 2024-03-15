package com.jspl.tickettaka.dto.response

import com.jspl.tickettaka.model.SeatInfo

data class SeatInfoResDto (
    val id: Long?,
    val seatNum: Int
) {
    companion object {
        fun fromEntities(seatInfo: List<SeatInfo>): List<SeatInfoResDto> {
            return seatInfo.map {
                val dto = SeatInfoResDto(
                    it.id,
                    it.seatNumber
                )

                dto
            }
        }
    }
}