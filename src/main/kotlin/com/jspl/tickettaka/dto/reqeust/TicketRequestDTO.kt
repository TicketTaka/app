package com.jspl.tickettaka.dto.reqeust

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

data class TicketRequestDTO(
    //금액
    @Column(name = "price_info")
    val priceInfo : Int,

    //좌석정보
    @Column(name = "set_Info")
    val setInfo : String,

    //예매된 시간
    @Column(name = "reserved_time")
    val reservedTime :String
)
