package com.jspl.tickettaka.model

import com.jspl.tickettaka.dto.response.TicketResponse
import jakarta.persistence.*


@Entity
@Table
class Ticket(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    //회원 아이디
    @Column(name = "member_id")
    val memberId: Long,

    //공연아이디
    @Column(name ="performance_instance_id")
    var performanceInstanceId:Long?,

    //공연이름
    @Column(name = "performance_name")
    var performanceName: String,

    //금액
    @Column(name = "price_info")
    val priceInfo: String,

    //좌석Id
    @Column(name = "seat_id")
    var seatId: Long?,

    //좌석번호
    @Column(name = "seat_number")
    var setInfo: String,

    //예매된 시간
    @Column(name = "reserved_time")
    var reservedTime: String

) {
}

fun Ticket.toResponse(): TicketResponse {
    return TicketResponse(
        id = id,
        //회원 아이디
        memberId = memberId,
        //공연 회차 아이디
        performanceInstanceId = performanceInstanceId,
        //공연이름
        performanceName = performanceName,
        //금액
        priceInfo = priceInfo,
        //좌석정보
        setInfo = setInfo,
        //예매된 시간
        reservedTime = reservedTime,
    )
}