package com.jspl.tickettaka.repository

import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.*

interface EmitterRepository {
    fun save(eventId: String, sseEmitter: SseEmitter): SseEmitter
    fun findById(memberId: String): Optional<SseEmitter>
    fun deleteById(eventId: String)
}