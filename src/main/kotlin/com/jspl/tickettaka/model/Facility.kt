package com.jspl.tickettaka.model

import jakarta.persistence.*

@Entity
@Table(name = "facilities")
class Facility(
    name: String,
    uniqueId: String,
    detailCnt: String,
    character: String,
    location: String,
    seatScale: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val facilityId: Long? = null

    @Column(name = "name")
    val name: String = name

    @Column(name = "unique_id")
    val uniqueId: String = uniqueId

    @Column(name = "detail_count")
    val detailCnt: String = detailCnt

    @Column(name = "character")
    val character: String = character

    @Column(name = "area")
    val location: String = location

    @Column(name = "seat_scale")
    val seatScale: String = seatScale


//    @OneToMany(mappedBy = "facility", cascade = [CascadeType.REMOVE])
//    var facilityDetailList: MutableList<FacilityDetail> = mutableListOf()

//    fun addFacilityDetail(facilityDetail: FacilityDetail) {
//        this.facilityDetailList.add(facilityDetail)
//    }
}