package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.SeatInfo
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface SeatInfoRepository :JpaRepository<SeatInfo,Long>{
    fun findByPerformanceInstanceId(id : Long):SeatInfo
    fun findFirstByPerformanceInstanceId(id :Long) :SeatInfo?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SeatInfo s where s.id = :id")
    fun findWithLockById(id: Long) :SeatInfo?

    fun findByPerformanceInstanceIdAndAvailability(id:Long, availability:Boolean):List<SeatInfo>


    fun countByPerformanceInstanceId(id:Long):Boolean
    fun countByPerformanceInstanceIdAndAvailability(id:Long, availability:Boolean):Int

}