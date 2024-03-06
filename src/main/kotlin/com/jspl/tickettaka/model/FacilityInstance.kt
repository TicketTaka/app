package com.jspl.tickettaka.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "facility_instance")
class FacilityInstance(
    facilityDetail: FacilityDetail,
    date: LocalDate
) {

    init {
        facilityDetail.addInstance(this)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val facilityInstanceId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_detail_id")
    val facilityDetail: FacilityDetail = facilityDetail

    @OneToMany(mappedBy = "facilityInstance", cascade = [CascadeType.REMOVE])
    var performanceInstanceList: MutableList<PerformanceInstance> = mutableListOf()

    @Column(name = "date")
    val date: LocalDate = date

    @Column(name = "availability")
    var availability: Boolean = true

    fun addPerformanceInstance(performanceInstance: PerformanceInstance) {
        performanceInstanceList.add(performanceInstance)
    }
}