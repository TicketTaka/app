package com.jspl.tickettaka.model

import jakarta.persistence.*


@Entity
@Table(name = "seat_info")
class SeatInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    //공연장 회자 아이디
    @Column(name = "performance_instance_id")
    val performanceInstanceId :Long,

    //좌석 번호
    @Column(name = "seat_number")
    val seatNumber :Int,

    //좌석 이름
    @Column(name = "seat_name")
    val seat_name : String,

    //예매 가능 여부
    @Column(name = "availability")
    val availability : Boolean
) {

}