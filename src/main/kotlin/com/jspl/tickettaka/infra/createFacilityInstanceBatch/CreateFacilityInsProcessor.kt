package com.jspl.tickettaka.infra.createFacilityInstanceBatch

import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.FacilityInstance
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class CreateFacilityInsProcessor(): ItemProcessor<FacilityDetail?, List<FacilityInstance>> {
    override fun process(item: FacilityDetail): List<FacilityInstance> {
        var today: LocalDate = LocalDate.now()
        val endDate: LocalDate = today.plusMonths(1)
        val answerList: MutableList<FacilityInstance> = mutableListOf()

        while (today != endDate) {
            val formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            val parsedDate = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy.MM.dd"))

            val facilityInstance = FacilityInstance(item, parsedDate)
            answerList.add(facilityInstance)

            today = today.plusDays(1)
        }

        return answerList
    }
}