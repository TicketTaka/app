package com.jspl.tickettaka.infra.batch.createPerformanceInstanceBatch

import com.jspl.tickettaka.model.FacilityInstance
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.model.PerformanceInstance
import com.jspl.tickettaka.repository.FacilityDetailRepository
import com.jspl.tickettaka.repository.FacilityInstanceRepository
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
class CreatePerformanceInsProcessor(
    private val facilityDetailRepository: FacilityDetailRepository,
    private val facilityInstanceRepository: FacilityInstanceRepository
): ItemProcessor<Performance, MutableList<Pair<FacilityInstance, PerformanceInstance>>> {
    override fun process(item: Performance): MutableList<Pair<FacilityInstance, PerformanceInstance>>? {
        val today = LocalDate.now()
        val lastDate = today.plusMonths(1)
        val facilityId = item.locationId
        val answerList: MutableList<Pair<FacilityInstance, PerformanceInstance>> = mutableListOf()
        val random = Random()

        var currentDate = item.startDate
        if (currentDate < today) {
            currentDate = today
        }
        while (currentDate <= item.endDate) {
            if (item.endDate > lastDate) {
                break
            }
            val concertHalls = facilityDetailRepository.findAllByFacilityId(facilityId)
            val possibleFacilityCnt = concertHalls.size
            var index = 0
            if(possibleFacilityCnt > 1) {
                index = random.nextInt(concertHalls.size)
            }
            val facilityDetail = concertHalls[index]
            println(facilityDetail.facilityDetailId)
            val facilityInstance =
                facilityInstanceRepository.findFacilityInstanceByFacilityDetailWithDate(facilityDetail, currentDate) ?: break

            val performanceInstance = PerformanceInstance(
                item.title,
                item.uniqueId,
                facilityInstance,
                facilityInstance.facilityDetail.facilityDetailName,
                currentDate,
                facilityInstance.facilityDetail.seatCnt.toLong()
            )

            val pair = Pair(facilityInstance, performanceInstance)

            answerList.add(pair)
            currentDate = currentDate.plusDays(1)
        }

        return answerList
    }
}