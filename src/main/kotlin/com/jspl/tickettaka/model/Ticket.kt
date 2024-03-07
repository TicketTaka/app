package com.jspl.tickettaka.model

import jakarta.persistence.*


@Entity
@Table
class Ticket(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    //회원 아이디
    @Column(name = "member_id")
    val memberId : Long,

    //공연 회차 아이디
    @Column(name = "performance_instance_id")
    val performanceInstanceId :Long,

    //금액
    @Column(name = "price_info")
    val priceInfo : Int,

    //좌석정보
    @Column(name = "set_Info")
    val setInfo : String,

    //예매된 시간
    @Column(name = "reserved_time")
    val reservedTime :String

) {
}