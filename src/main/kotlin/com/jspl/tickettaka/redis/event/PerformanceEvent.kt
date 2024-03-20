package com.jspl.tickettaka.redis.event

import com.jspl.tickettaka.model.Performance
import org.springframework.context.ApplicationEvent

class PerformanceEvent(source: Any, val performance: Performance): ApplicationEvent(source){
    init {
        requireNotNull(performance) { "Performance must not be null" }
    }
}