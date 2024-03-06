package com.jspl.tickettaka.data

import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.model.PerformanceInstance
import com.jspl.tickettaka.repository.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.contracts.contract

@Component
class PerformanceDataCrawling(
    private val performanceRepository: PerformanceRepository,
    private val performanceInstanceRepository: PerformanceInstanceRepository,
    private val facilityRepository: FacilityRepository,
    private val facilityDetailRepository: FacilityDetailRepository,
    private val facilityInstanceRepository: FacilityInstanceRepository,
    @Value("\${data.secret.key}")
    private val secretKey: String
) {
    @Transactional
    fun execute(){
        val firstApiUrl = "https://www.kopis.or.kr/openApi/restful/pblprfr"
        val serviceKey = secretKey
        val params01 = mapOf(
            "service" to serviceKey,
            "stdate" to "20240301",
            "eddate" to "20240401",
            "cpage" to "1",
            "rows" to "2000",
            "prfstate" to "01",
            "newsql" to "Y"
        )
        val params02 = mapOf(
            "service" to serviceKey,
            "stdate" to "20240301",
            "eddate" to "20240401",
            "cpage" to "1",
            "rows" to "2000",
            "prfstate" to "02",
            "newsql" to "Y"
        )

        val xmlData01 = sendGetRequest(firstApiUrl, params01)
        val xmlData02 = sendGetRequest(firstApiUrl, params02)

        val allPerformance01 = mutableListOf<Performance>()
        val allPerformance02 = mutableListOf<Performance>()

        if(xmlData01 != null && xmlData02 != null) {
            val mt20ids01 = parseXmlForMt20Ids(xmlData01)
            val mt20ids02 = parseXmlForMt20Ids(xmlData02)

            mt20ids01.forEach { mt20id ->
                val secondApiUrl = "https://www.kopis.or.kr/openApi/restful/pblprfr/$mt20id"
                val secondApiParams = mapOf("service" to serviceKey, "newsql" to "Y")
                val secondApiResponse = sendGetRequest(secondApiUrl, secondApiParams)

                extractAndProcessData(secondApiResponse)?.let { allPerformance01.add(it) }
            }
            println(allPerformance01.size)

            performanceRepository.saveAll(allPerformance01)

            mt20ids02.forEach { mt20id ->
                val secondApiUrl = "https://www.kopis.or.kr/openApi/restful/pblprfr/$mt20id"
                val secondApiParams = mapOf("service" to serviceKey, "newsql" to "Y")
                val secondApiResponse = sendGetRequest(secondApiUrl, secondApiParams)

                extractAndProcessData(secondApiResponse)?.let { allPerformance02.add(it) }
            }
            println(allPerformance02.size)

            performanceRepository.saveAll(allPerformance02)
        } else {
            println("Failed to fetch XML data from the first API.")
        }
    }

    @Transactional
    fun createInstance() {
        val allPerformance = performanceRepository.findAll()
        for (performance in allPerformance) {
            val today = LocalDate.now()
            val lastDate = today.plusMonths(1)
            val startDate = performance.startDate
            val endDate = performance.endDate
            val facilityId = performance.locationId

            val dateRange = if (today >= startDate && lastDate > endDate) {
                Pair(today, endDate)
            } else if (today >= startDate && lastDate <= endDate) {
                Pair(today, lastDate)
            } else if(today < startDate && lastDate > endDate) {
                Pair(startDate, endDate)
            } else {
                Pair(startDate, lastDate)
            }

            val random = Random()

            var currentDate = dateRange.first
            while (currentDate != dateRange.second.plusDays(1)) {
                val concertHalls = facilityDetailRepository.findAllByFacilityId(facilityId)
                var possibleFacilityCnt = concertHalls.size
//                val concertHalls = facilityInstanceRepository.findFacilityInstanceByFacilityName(facilityId, currentDate)
                var index = 0
                if(possibleFacilityCnt != 1) {
                    index = random.nextInt(concertHalls.size)
                }
                val facilityDetail = concertHalls[index]
                println(facilityDetail.facilityDetailId)
                val facilityInstance = facilityInstanceRepository.findFacilityInstanceByFacilityDetailWithDate(facilityDetail, currentDate)
                val performanceInstance = facilityInstance?.let {
                    PerformanceInstance(
                        performance.title,
                        performance.uniqueId,
                        it,
                        facilityInstance.facilityDetail.facilityDetailName,
                        currentDate,
                        facilityInstance.facilityDetail.seatCnt.toLong()
                    )
                }

                if (facilityInstance != null) {
                    facilityInstance.availability = false
                }

                if (facilityInstance != null) {
                    facilityInstanceRepository.save(facilityInstance)
                }
                if (performanceInstance != null) {
                    performanceInstanceRepository.save(performanceInstance)
                }

                currentDate = currentDate.plusDays(1)
            }
        }
    }
    private fun sendGetRequest(url: String, params: Map<String, String>): String? {
        val urlBuilder = StringBuilder(url)
        urlBuilder.append("?")
        params.forEach { (key, value) ->
            urlBuilder.append("$key=$value&")
        }
        val urlString = urlBuilder.toString().removeSuffix("&")

        return try {
            val connection = URL(urlString).openConnection()
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            response
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseXmlForMt20Ids(xmlData: String): List<String> {
        val doc: Document = Jsoup.parse(xmlData)
        val mt20Ids = mutableListOf<String>()
        val elements: List<Element> = doc.select("mt20id")

        for (element in elements) {
            mt20Ids.add(element.text())
        }
        return mt20Ids
    }

    private fun extractAndProcessData(xmlData: String?): Performance? {
        val MAX_LENGTH = 255
        if (xmlData != null) {
            val doc: Document = Jsoup.parse(xmlData)
            val prfnm = doc.selectFirst("prfnm")?.text()
            val prfpdfrom = doc.selectFirst("prfpdfrom")?.text()
            val prfpdto = doc.selectFirst("prfpdto")?.text()
            val fcltynm = doc.selectFirst("fcltynm")?.text()
            val pcseguidance = doc.selectFirst("pcseguidance")?.text()
            val genrenm = doc.selectFirst("genrenm")?.text()
            val prfstate = doc.selectFirst("prfstate")?.text()
            val mt20id = doc.selectFirst("mt20id")?.text()

            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val formatStartDate: LocalDate = LocalDate.parse(prfpdfrom, formatter)
            val formatEndDate: LocalDate = LocalDate.parse(prfpdto, formatter)

            val pattern1 = "\\s*\\(.*?\\)".toRegex()
            val replaceName = fcltynm?.replace(pattern1, "")

            val pattern2 = "\\)$".toRegex()
            val resultName = replaceName?.replace(pattern2, "")

//            val pattern = "\\s*\\([^)]*\\)\\s*".toRegex()
//            val result = fcltynm?.replace(pattern, "")
//            val findName = result?.trim()
//            println(findName)
//            val facilityId = findName?.let { facilityRepository.findIdByNameString(it) }

            val resultList = resultName?.let { facilityRepository.findIdByNameString(it) }
            var result: String? = null
            if(resultList != null){
                result = if(resultList.size == 1) {
                    resultList[0]
                } else {
                    null
                }
            }


            if (pcseguidance != null && pcseguidance.length <= MAX_LENGTH && prfstate != null && result != null) {
                return Performance(
                        title = prfnm ?: "",
                        uniqueId = mt20id ?: "",
                        location = resultName ?: "",
                        locationId = result,
                        startDate = formatStartDate,
                        endDate = formatEndDate,
                        genre = genrenm ?: "",
                        priceInfo = pcseguidance,
                        state = prfstate
                    )
            }
        } else {
            println("Failed to fetch XML data from the second API.")
        }
        return null
    }
}