package com.jspl.tickettaka.notification

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap
import java.util.*

@Service
class EmitterService(private val emitterRepository: EmitterRepository) {
    private val emitters: MutableMap<String, SseEmitter> = ConcurrentHashMap()
    private val log = LoggerFactory.getLogger(EmitterService::class.java)

    fun save(eventId: String, sseEmitter: SseEmitter): SseEmitter {
        emitters[eventId] = sseEmitter
        return sseEmitter
    }

    fun findById(memberId: String): Optional<SseEmitter> {
        return Optional.ofNullable(emitters[memberId])
    }

    fun deleteById(eventId: String) {
        emitters.remove(eventId)
    }
}