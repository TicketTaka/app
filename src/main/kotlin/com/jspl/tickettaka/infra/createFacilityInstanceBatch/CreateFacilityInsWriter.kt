package com.jspl.tickettaka.infra.createFacilityInstanceBatch

import com.jspl.tickettaka.model.FacilityInstance
import com.jspl.tickettaka.repository.FacilityInstanceRepository
import com.jspl.tickettaka.repository.PerformanceRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class CreateFacilityInsWriter(
    private val facilityInstanceRepository: FacilityInstanceRepository,
): ItemWriter<List<FacilityInstance>> {
    override fun write(chunk: Chunk<out List<FacilityInstance>>) {
        for(chunkItems in chunk) {
            for(facilityInstance in chunkItems) {
                facilityInstanceRepository.save(facilityInstance)
            }
        }
    }
}