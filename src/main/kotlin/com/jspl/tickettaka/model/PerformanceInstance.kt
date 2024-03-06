package com.jspl.tickettaka.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "performance_instance")
class PerformanceInstance(
    performanceName: String,
    performanceUniqueId: String,
    facilityInstance: FacilityInstance,
    session: String,
    date: LocalDate,
    remainSeat: Long,
) {

    init {
        facilityInstance.addPerformanceInstance(this)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val performanceInstanceId: Long? = null

    @Column(name = "performanceName")
    val performanceName: String = performanceName

    @Column(name = "performance_unique_id")
    val performanceUniqueId: String = performanceUniqueId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_instance_id")
    val facilityInstance: FacilityInstance = facilityInstance

    @Column(name = "session")
    val session: String = session

    @Column(name = "date")
    val date: LocalDate = date

    @Column(name = "remain_seat")
    val remainSeat: Long = remainSeat
}