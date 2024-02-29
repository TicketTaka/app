package com.jspl.tickettaka.model

import jakarta.persistence.*

@Entity
@Table(name = "facilities")
class Facility(
    name: String,
    uniqueId: String,
    detailCnt: String,
    character: String,
    sido: String,
    gugun: String,
    locationDetail: String,
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

    @Column(name = "location_1")
    val sido: String = sido

    @Column(name = "location_2")
    val gugun: String = gugun

    @Column(name = "area")
    val location: String = locationDetail

    @Column(name = "seat_scale")
    val seatScale: String = seatScale


//    @OneToMany(mappedBy = "facility", cascade = [CascadeType.REMOVE])
//    var facilityDetailList: MutableList<FacilityDetail> = mutableListOf()

//    fun addFacilityDetail(facilityDetail: FacilityDetail) {
//        this.facilityDetailList.add(facilityDetail)
//    }
}