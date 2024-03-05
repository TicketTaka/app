package com.jspl.tickettaka.model

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDate

@Entity
@Table(name = "performances")
class Performance(
    title: String,
    uniqueId: String,
    location: String,
    locationId:String?,
    startDate: LocalDate,
    endDate: LocalDate,
    genre: String,
    priceInfo: String,
    state: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val performanceId: Long? =null

    @Column(name = "title")
    val title:String = title

    @Column(name = "unique_id")
    val uniqueId: String = uniqueId

    @Column(name = "location")
    val location:String = location

    @Column(name = "location_id")
    val locationId:String? = locationId

    @Column(name = "start_date")
    val startDate: LocalDate = startDate

    @Column(name = "end_date")
    val endDate: LocalDate =  endDate

    @Column(name = "genre")
    val genre:String = genre

    @Column(name = "price_info")
    val priceInfo:String = priceInfo

    @Column(name = "state")
    val state:String = state
}