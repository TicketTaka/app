package com.jspl.tickettaka.model

import jakarta.persistence.*


@Entity
@Table(name = "seat_info")
class SeatInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 공연 인스턴스
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_instance_id")
    val performanceInstance: PerformanceInstance,

    // 좌석 번호
    @Column(name = "seat_number")
    val seatNumber: Int,

    // 가격
    @Column(name = "price")
    val price: String,

    // 예매 가능 여부
    @Column(name = "availability")
    var availability: Boolean
)




////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//@Entity
//@Table(name = "seat_info")
//class SeatInfo(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Long? = null,
//
//    //콘서트 이름
//    @Column(name = "performanceName")
//    val performanceName: String,
//
//    //콘서트 정보(고유번호)
//    @Column(name = "performance_uniqueId")
//    val uniqueId: String,
//
//    //공연장 회자 아이디
//    @Column(name = "performance_instance_id")
//    val performanceInstanceId: Long?,
//
//    //좌석 번호
//    @Column(name = "seat_number")
//    val seatNumber: Int,
//
//    //가격
//    @Column(name = "price")
//    val price: String,
//
//    //예매 가능 여부
//    @Column(name = "availability")
//    var availability: Boolean
//
//) {
//
//}