package com.jspl.tickettaka.service

import com.jspl.tickettaka.repository.EmitterRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap
import java.util.*

@Service
class EmitterService: EmitterRepository {
    private val emitters: MutableMap<String, SseEmitter> = ConcurrentHashMap()
    private val log = LoggerFactory.getLogger(EmitterService::class.java)

    override fun save(eventId: String, sseEmitter: SseEmitter): SseEmitter {
        emitters[eventId] = sseEmitter
        return sseEmitter
    }

    override fun findById(memberId: String): Optional<SseEmitter> {
        return Optional.ofNullable(emitters[memberId])
    }

    override fun deleteById(eventId: String) {
        emitters.remove(eventId)
    }
}