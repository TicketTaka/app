package com.jspl.tickettaka.model

import jakarta.persistence.*

@Entity
@Table(name = "facility_details")
class FacilityDetail(
    facilityDetailName: String,
    seatCnt: String,
//    facility: Facility
    facilityName: String
) {

//    init {
//        facility.addFacilityDetail(this)
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val facilityDetailId: Long? = null

    @Column(name = "facility_detail_name")
    val facilityDetailName:String = facilityDetailName

    @Column(name = "seat_count")
    val seatCnt: String = seatCnt

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "facility_id")
//    val facility: Facility = facility
    @Column(name = "facility_name")
    val facilityName: String = facilityName
}