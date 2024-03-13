package com.jspl.tickettaka.controller

import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.redis.RedisPublisher
import com.jspl.tickettaka.service.PerformanceService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class PerformanceApi(
    private val performanceService: PerformanceService,
) {
    @GetMapping("/update-performances")
    fun update(@RequestBody performance: Performance) {
        performanceService.updatePerformance(performance)
        println("complete")

//        //장르에 따라 채널 생성
//        val channel = "performance_${performance.genre}"
//        //생성된 채널에서 구독자에게 새 공연등록 알림 전송
//        val message = "New performance registered: ${performance.title}"
//        redisPublisher.publishMessage(channel, message)
    }
}