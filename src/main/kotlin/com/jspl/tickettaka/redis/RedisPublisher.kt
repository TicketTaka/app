package com.jspl.tickettaka.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisPublisher(private val redisTemplate: RedisTemplate<String, String>) {
    fun publishMessage(channel: String, message: String) {
        redisTemplate.convertAndSend(channel, message)
    }
}