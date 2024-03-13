package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Member
import org.springframework.data.jpa.repository.JpaRepository


interface MemberRepository :JpaRepository<Member,Long>{
    fun findByEmail(email:String):Member?
}