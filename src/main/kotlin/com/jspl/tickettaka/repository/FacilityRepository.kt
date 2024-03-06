package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Facility
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FacilityRepository:JpaRepository<Facility, Long> {
    @Query("SELECT f.uniqueId from Facility f WHERE f.name = :name")
    fun findIdByNameString(name: String): List<String>

    @Query("select distinct f.sido from Facility f")
    fun findLocation1(): List<String>

    @Query("select DISTINCT f.gugun from Facility f where f.sido = :name")
    fun findLocation2(name: String): List<String>

    @Query("select f from Facility f where f.sido = :location1 and f.gugun = :location2")
    fun findFacilityOfLocation(location1: String, location2: String): List<Facility>
}