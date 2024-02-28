package com.jspl.tickettaka.model

import jakarta.persistence.*

@Entity
@Table(name = "performances")
class Performance(
    title: String,
    location: String,
    locationId:String?,
    startDate: String,
    endDate: String,
    genre: String,
    priceInfo: String,
    state: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val performanceId: Long? =null

    @Column(name = "title")
    val title:String = title

    @Column(name = "location")
    val location:String = location

    @Column(name = "location_id")
    val locationId:String? = locationId

    @Column(name = "start_date")
    val startDate:String = startDate

    @Column(name = "end_date")
    val endDate:String =  endDate

    @Column(name = "genre")
    val genre:String = genre

    @Column(name = "price_info")
    val priceInfo:String = priceInfo

    @Column(name = "state")
    val state:String = state
}