package com.jspl.tickettaka.service

import com.jspl.tickettaka.data.PerformanceDataCrawling
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class PerformanceService @Autowired constructor(
    private val performanceDataCrawling: PerformanceDataCrawling,
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun updatePerformance(genre: String, title: String) {
        performanceDataCrawling.execute()

        val channel = "genre:$genre"
        redisTemplate.opsForList().rightPushIfPresent("channels", channel)

        val message = "새로운 공연이 등록되었습니다: $title"
        redisTemplate.convertAndSend(channel, message)
    }
}