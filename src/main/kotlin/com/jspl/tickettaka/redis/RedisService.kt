package com.jspl.tickettaka.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val ops = redisTemplate.opsForZSet()
    fun dequeue(performanceId: String, memberId: String){
        ops.remove("P${performanceId}", memberId)
    }
}