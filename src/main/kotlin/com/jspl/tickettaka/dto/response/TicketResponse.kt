package com.jspl.tickettaka.dto.response

data class TicketResponse(
    val id :Long?,
    //회원 아이디
    val memberId :Long,
    //공연 회차 아이디
    val performanceInstanceId :Long?,
    //공연 이름
    val performanceName :String,
    //금액
    val priceInfo  :String,
    //좌석정보
    val setInfo :String,
    //예매된 시간
    val reservedTime :String
)
