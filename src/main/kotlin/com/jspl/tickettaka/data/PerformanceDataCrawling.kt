package com.jspl.tickettaka.data

import com.jspl.tickettaka.dto.response.PerformanceResDto
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.PerformanceRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL

@Component
class PerformanceDataCrawling(
    private val performanceRepository: PerformanceRepository,
    @Value("\${data.secret.key}")
    private val secretKey: String
) {
    fun execute(){
        val firstApiUrl = "https://www.kopis.or.kr/openApi/restful/pblprfr"
        val serviceKey = secretKey
        val params01 = mapOf(
            "service" to serviceKey,
            "stdate" to "20240224",
            "eddate" to "20240224",
            "cpage" to "1",
            "rows" to "2000",
            "prfstate" to "01",
            "newsql" to "Y"
        )
        val params02 = mapOf(
            "service" to serviceKey,
            "stdate" to "20240224",
            "eddate" to "20240224",
            "cpage" to "1",
            "rows" to "2000",
            "prfstate" to "02",
            "newsql" to "Y"
        )

        val xmlData01 = sendGetRequest(firstApiUrl, params01)
        val xmlData02 = sendGetRequest(firstApiUrl, params02)

        val allPerformances = mutableListOf<Performance>()

        if(xmlData01 != null && xmlData02 != null) {
            val mt20ids01 = parseXmlForMt20Ids(xmlData01)
            val mt20ids02 = parseXmlForMt20Ids(xmlData02)

            val mt20ids = mt20ids01 + mt20ids02

            mt20ids.forEach { mt20id ->
                val secondApiUrl = "https://www.kopis.or.kr/openApi/restful/pblprfr/$mt20id"
                val secondApiParams = mapOf("service" to serviceKey, "newsql" to "Y")
                val secondApiResponse = sendGetRequest(secondApiUrl, secondApiParams)

                extractAndProcessData(secondApiResponse)?.let { allPerformances.add(it) }
            }
            println(allPerformances.size)

            performanceRepository.saveAll(allPerformances)
        } else {
            println("Failed to fetch XML data from the first API.")
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

            if (pcseguidance != null && pcseguidance.length <= MAX_LENGTH && prfstate != null) {
                return Performance(
                    title = prfnm ?: "",
                    location = fcltynm ?: "",
                    startDate = prfpdfrom ?: "",
                    endDate = prfpdto ?: "",
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