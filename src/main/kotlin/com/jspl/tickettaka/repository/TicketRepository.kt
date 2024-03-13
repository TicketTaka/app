package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository:JpaRepository<Ticket,Long> {
    fun findByMemberId(id : Long) : List<Ticket>?

    fun findBySeatId(id :Long) :Ticket
}