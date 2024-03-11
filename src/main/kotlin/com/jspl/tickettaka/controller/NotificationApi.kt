package com.jspl.tickettaka.controller

import com.jspl.tickettaka.redis.RedisPublisher
import com.jspl.tickettaka.redis.RedisSubscriber
import lombok.RequiredArgsConstructor
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.time.Duration

@RequestMapping("/users/redis")
@RestController
@RequiredArgsConstructor
class NotificationApi(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val redisPublisher: RedisPublisher,
    private val redisSubscriber: RedisSubscriber
) {
    companion object {
        const val NOTIFICATION_CHANNEL = "notifications"
    }

    @GetMapping("/stream")
    fun streamNotifications(): Flux<ServerSentEvent<String>> {
        val messageFlux = redisTemplate.listenTo(ChannelTopic(NOTIFICATION_CHANNEL))
            .map { it.message }
            .map { ServerSentEvent.builder(it).build() }

        return Flux.interval(Duration.ofSeconds(1L)) // Test to keep connection alive
            .map { ServerSentEvent.builder<String>().comment("Test").build() }
            .mergeWith(messageFlux)
    }

    @PostMapping("/publish/{channel}")
    fun publishMessage(@PathVariable channel: String, @RequestBody message: String) {
        redisPublisher.publishMessage(channel, message)
    }

    @GetMapping("/subscribe/{channel}")
    fun subscribeToChannel(@PathVariable channel: String) {
        redisSubscriber.subscribeToChannel(channel)
    }
}