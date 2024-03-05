package com.jspl.tickettaka.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "performance_instance")
class PerformanceInstance(
    performanceName: String,
    performanceUniqueId: String,
    concertHallName: String,
    facilityInstanceId: Long,
    session: String,
    date: LocalDate,
    remainSeat: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val performanceInstanceId: Long? = null

    @Column(name = "performanceName")
    val performanceName: String = performanceName

    @Column(name = "performance_unique_id")
    val performanceUniqueId: String = performanceUniqueId

    @Column(name = "concert_hall_name")
    val concertHallName: String = concertHallName

    @Column(name = "facility_instance_id")
    val facilityInstanceId: Long = facilityInstanceId

    @Column(name = "session")
    val session: String = session

    @Column(name = "date")
    val date: LocalDate = date

    @Column(name = "remain_seat")
    val remainSeat: Long = remainSeat
}