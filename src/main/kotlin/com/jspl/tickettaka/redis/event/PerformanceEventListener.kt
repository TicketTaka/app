package com.jspl.tickettaka.redis.event

import com.jspl.tickettaka.redis.RedisPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PerformanceEventListener(private val redisPublisher: RedisPublisher) {
    @EventListener
    fun handleEvent(event: PerformanceEvent) {
        val performance = event.performance
        val channelId = "performance:${performance.performanceId}" //공연별로 고유채널 생성

        //새로운 채널을 만들고 알림 메시지를 전송
        redisPublisher.publishMessage(channelId, "새로운 공연이 등록되었습니다: ${performance.title}")
    }
}