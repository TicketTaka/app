package com.jspl.tickettaka.dto.response

import java.time.LocalDate
import java.util.Date

data class TempPerfomanceDate(
    val date :LocalDate,
    val seatNumber : List<String>
)
