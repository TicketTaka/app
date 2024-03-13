package com.jspl.tickettaka.model

import jakarta.persistence.*

@Entity
@Table(name = "facility_details")
class FacilityDetail(
    facilityDetailName: String,
    seatCnt: String,
    facilityName: String,
    facilityId: String
) {

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

    @Column(name = "facility_id")
    val facilityId: String = facilityId

    @OneToMany(mappedBy = "facilityDetail", cascade = [CascadeType.REMOVE])
    var instanceList: MutableList<FacilityInstance> = mutableListOf()

    fun addInstance(facilityInstance: FacilityInstance) {
        instanceList.add(facilityInstance)
    }
}