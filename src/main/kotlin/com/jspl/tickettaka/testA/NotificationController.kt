package com.jspl.tickettaka.testA

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val redisTemplate: RedisTemplate<String, String>
): MessageListener {
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    init {
        redisTemplate.connectionFactory?.connection?.subscribe(this, "notifications".toByteArray())
    }

    @GetMapping("/subscribe", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribe(): SseEmitter {
        val emitter = SseEmitter()
        emitters.add(emitter)

        emitter.onCompletion {
            emitters.remove(emitter)
        }

        return emitter
    }

    @GetMapping("/publish")
    fun publish(message: String) {
        redisTemplate.convertAndSend("notifications", message)
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val messageContent = String(message.body)
        emitters.forEach { emitter -> emitter.send(SseEmitter.event().data(messageContent)) }
    }

    @GetMapping("/test")
    fun test() {
        publish("Test message")
    }
}