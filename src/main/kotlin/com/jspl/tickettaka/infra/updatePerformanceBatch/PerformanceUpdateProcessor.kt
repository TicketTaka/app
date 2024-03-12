package com.jspl.tickettaka.infra.updatePerformanceBatch

import com.jspl.tickettaka.model.Performance
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class PerformanceUpdateProcessor(): ItemProcessor<Performance?, Performance?> {
    override fun process(item: Performance): Performance? {
        item.state = "공연중"

        return item
    }

}