package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Facility
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FacilityRepository:JpaRepository<Facility, Long> {
    @Query("SELECT f.name FROM Facility f")
    fun findAllNames(): List<String>

    @Query("SELECT f.uniqueId from Facility f WHERE f.name = :name")
    fun findIdByNameString(name: String): List<String>


    fun findByName(name: String): Facility?
}