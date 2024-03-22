package com.jspl.tickettaka.infra.batch.createPerformanceInstanceBatch

import com.jspl.tickettaka.model.FacilityInstance
import com.jspl.tickettaka.model.PerformanceInstance
import com.jspl.tickettaka.repository.FacilityInstanceRepository
import com.jspl.tickettaka.repository.PerformanceInstanceRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class CreatePerformanceInsWriter(
    private val performanceInstanceRepository: PerformanceInstanceRepository,
): ItemWriter<MutableList<Pair<FacilityInstance, PerformanceInstance>>> {
    override fun write(chunk: Chunk<out MutableList<Pair<FacilityInstance, PerformanceInstance>>>) {
        for(pairList in chunk) {
            for(pair in pairList) {
                pair.first.availability = false
                performanceInstanceRepository.save(pair.second)
            }
        }
    }
}