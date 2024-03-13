package com.jspl.tickettaka.service

import com.jspl.tickettaka.data.PerformanceDataCrawling
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.redis.event.PerformanceEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class PerformanceService @Autowired constructor(
    private val performanceDataCrawling: PerformanceDataCrawling,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun updatePerformance(performance: Performance) {
        performanceDataCrawling.execute()
        //공연 등록 이벤트를 발행
        eventPublisher.publishEvent(PerformanceEvent(this, performance))
    }
}