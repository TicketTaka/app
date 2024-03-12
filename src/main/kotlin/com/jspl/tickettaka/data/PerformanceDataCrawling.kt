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
    fun fetchData(startDate: String, endDate: String, prfState: String): List<Performance>? {
        val apiUrl = "https://www.kopis.or.kr/openApi/restful/pblprfr"
        val serviceKey = secretKey
        val params = mapOf(
            "service" to serviceKey,
            "stdate" to startDate,
            "eddate" to endDate,
            "cpage" to "1",
            "rows" to "2000",
            "prfstate" to prfState,
            "newsql" to "Y"
        )
        val xmlData = sendGetRequest(apiUrl, params)
        val allPerformance = mutableListOf<Performance>()

        if (xmlData != null) {
            val mt20ids = parseXmlForMt20Ids(xmlData)
            mt20ids.forEach { mt20id ->
                val secondApiUrl = "https://www.kopis.or.kr/openApi/restful/pblprfr/$mt20id"
                val secondApiParams = mapOf("service" to serviceKey, "newsql" to "Y")
                val secondApiResponse = sendGetRequest(secondApiUrl, secondApiParams)
                extractAndProcessData(secondApiResponse)?.let { allPerformance.add(it) }
            }
        } else {
            println("Failed to fetch XML data from the API.")
        }
        return allPerformance.takeIf { it.isNotEmpty() }
    }

    fun execute(startDate: String, endDate: String) {
        val allPerformance01 = fetchData(startDate, endDate, "01")
        val allPerformance02 = fetchData(startDate, endDate, "02")

        if (allPerformance01 != null && allPerformance02 != null) {
            println(allPerformance01.size)
            performanceRepository.saveAll(allPerformance01)

            println(allPerformance02.size)
            performanceRepository.saveAll(allPerformance02)
        }
    }


    @Transactional
    fun createInstance() {
        val today = LocalDate.now()
        val lastDate = today.plusMonths(1)
        val allPerformance = performanceRepository.findAllByDate(today, lastDate)
        for (performance in allPerformance) {
            val facilityId = performance.locationId
            val random = Random()

            var currentDate = performance.startDate
            if (currentDate < today) {
                currentDate = today
            }
            while (currentDate <= performance.endDate) {
                if (performance.endDate > lastDate) {
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
                    performance.title,
                    performance.uniqueId,
                    facilityInstance,
                    facilityInstance.facilityDetail.facilityDetailName,
                    currentDate,
                    facilityInstance.facilityDetail.seatCnt.toLong()
                )

                performanceInstanceRepository.save(performanceInstance)
                facilityInstance.availability = false

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