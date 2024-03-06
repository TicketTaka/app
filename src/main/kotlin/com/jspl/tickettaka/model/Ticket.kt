package com.jspl.tickettaka.model

import jakarta.persistence.*


@Entity
@Table
class Ticket(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null

//    @회원아이디
//    @공연인스턴스
//
//
//    금액,좌석번호 ,예매시간

) {
}