package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.SeatInfo
import org.springframework.data.jpa.repository.JpaRepository

interface SeatInfoRepository :JpaRepository<SeatInfo,Long>{
    fun findByPerformanceInstanceId(id : Long):SeatInfo
    fun findFirstByPerformanceInstanceId(id :Long) :SeatInfo?

    fun findByPerformanceInstanceIdAndAvailability(id:Long, availability:Boolean):SeatInfo
    fun countByPerformanceInstanceId(id:Long):Boolean
    fun countByPerformanceInstanceIdAndAvailability(id:Long, availability:Boolean):Int

}