package com.jspl.tickettaka.dto.response

data class TicketResponse(
    //회원 아이디
    val memberId :Long,
    //공연 회차 아이디
    val performanceInstanceId :Long,
    //금액
    val priceInfo  :Int,
    //좌석정보
    val setInfo :String,
    //예매된 시간
    val reservedTime :String
)
