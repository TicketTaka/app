package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Member
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock


interface MemberRepository :JpaRepository<Member,Long>{
    fun findByEmail(email:String):Member?

    //test하기 위한 코드
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByUsername(name:String):Member
}