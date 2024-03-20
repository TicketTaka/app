package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.PerformanceInstance
import com.jspl.tickettaka.model.SeatInfo
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface SeatInfoRepository :JpaRepository<SeatInfo,Long>{

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("select s from SeatInfo s join fetch PerformanceInstance where s.id = :id")
//    fun findWithLockById(id: Long) :SeatInfo?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SeatInfo s join fetch s.performanceInstance pi where s.id = :id")
    fun findWithLockById(id: Long) :SeatInfo?

    fun findByPerformanceInstanceAndAvailability(performanceInstance: PerformanceInstance, availability: Boolean):List<SeatInfo>

}