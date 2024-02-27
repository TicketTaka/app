package com.jspl.tickettaka.data

import com.jspl.tickettaka.model.Facility
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.repository.FacilityDetailRepository
import com.jspl.tickettaka.repository.FacilityRepository
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

@Component
class FacilityDataCrawling(
    private val facilityRepository: FacilityRepository,
//    private val facilityDetailRepository: FacilityDetailRepository

    @Value("\${data.secret.key}")
    private val secretKey: String
) {
    @Transactional
    fun fetchAndSaveFacilities() {
        val firstApiUrl = "https://www.kopis.or.kr/openApi/restful/prfplc"
        val serviceKey = secretKey
        val params1 = mapOf(
            "service" to serviceKey,
            "cpage" to "1",
            "rows" to "3000",
            "fcltychartr" to "1"
        )
        val params2 = mapOf(
            "service" to serviceKey,
            "cpage" to "1",
            "rows" to "3000",
            "fcltychartr" to "2"
        )
        val params3 = mapOf(
            "service" to serviceKey,
            "cpage" to "1",
            "rows" to "3000",
            "fcltychartr" to "3"
        )

        val xmlData1 = sendGetRequest(firstApiUrl, params1)
        val xmlData2 = sendGetRequest(firstApiUrl, params2)
        val xmlData3 = sendGetRequest(firstApiUrl, params3)

        val allFacilities = mutableListOf<Facility>()

        if (xmlData1 != null && xmlData2 != null && xmlData3 != null) {
            val mt10id01 = parseXmlForMt10id(xmlData1)
            val mt10id02 = parseXmlForMt10id(xmlData2)
            val mt10id03 = parseXmlForMt10id(xmlData3)

            val mt10ids = mt10id01 + mt10id02 + mt10id03

            mt10ids.forEach { mt10id ->
                val secondApiUrl = "https://kopis.or.kr/openApi/restful/prfplc/$mt10id"
                val secondApiParams = mapOf("service" to serviceKey, "newsql" to "Y")
                val secondApiResponse = sendGetRequest(secondApiUrl, secondApiParams)

                extractAndProcessData(secondApiResponse)?.let { allFacilities.add(it) }
            }
        } else {
            println("Failed to fetch XML data from the first API.")
        }

        facilityRepository.saveAll(allFacilities)
    }

//    @Transactional
//    fun fetchAndProcessFacilitiesDetail() = runBlocking {
//        val serviceKey = secretKey
//        val facilityNames = facilityRepository.findAllNames()
//        println(facilityNames.size)
//
//        val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher() // 적절한 스레드 풀 크기 조정
//
//        val jobs = facilityNames.map { name ->
//            if (!name.contains(" ") && !name.contains("[") && !name.contains("]") && !name.contains("(") && !name.contains(")")) {
//                async(dispatcher) {
//                    println(name)
//                    val secondApiUrl = "https://kopis.or.kr/openApi/restful/prfstsPrfByFct"
//                    val secondApiParams = mapOf(
//                        "service" to serviceKey,
//                        "cpage" to "1",
//                        "rows" to "20",
//                        "stdate" to "20230101",
//                        "eddate" to "20240226",
//                        "shprfnmfct" to name,
//                        "newsql" to "Y"
//                    )
//                    val secondApiResponse = sendGetRequest(secondApiUrl, secondApiParams)
//                    extractAndProcessDetailData(secondApiResponse)
//                }
//            } else {
//                null
//            }
//        }
//
//        val facilityDetailsList = jobs.flatMap { it?.await() ?: emptyList() }
//
//        facilityDetailRepository.saveAll(facilityDetailsList)
//
//        dispatcher.close()
//    }

    private fun sendGetRequest(url: String, params: Map<String, String>): String? {
        val urlBuilder = StringBuilder(url)
        urlBuilder.append("?")
        params.forEach { (key, value) ->
            urlBuilder.append("$key=$value&")
        }
        val urlString = urlBuilder.toString().removeSuffix("&")

        return try {
            val connection = URL(urlString).openConnection()
            connection.connectTimeout = 50000
            connection.readTimeout = 50000
            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            response
        } catch (e: Exception) {
            println("Failed to send HTTP request: ${e.message}")
            null
        }
    }

    private fun parseXmlForMt10id(xmlData: String): List<String> {
        val doc: Document = Jsoup.parse(xmlData)
        val mt10ids = mutableListOf<String>()
        val elements: List<Element> = doc.select("mt10id")

        for (element in elements) {
            mt10ids.add(element.text())
        }
        return mt10ids
    }

    private fun extractAndProcessData(xmlData: String?): Facility? {
        if (xmlData != null) {
            val doc: Document = Jsoup.parse(xmlData)
            val fcltynm = doc.selectFirst("fcltynm")?.text()
            val mt10id = doc.selectFirst("mt10id")?.text()
            val mt13cnt = doc.selectFirst("mt13cnt")?.text()
            val fcltychartr = doc.selectFirst("fcltychartr")?.text()
            val adres = doc.selectFirst("adres")?.text()
            val seatscale = doc.selectFirst("seatscale")?.text()

            return Facility(
                name = fcltynm ?: "",
                uniqueId = mt10id ?: "",
                detailCnt = mt13cnt ?: "",
                character = fcltychartr ?: "",
                location = adres ?: "",
                seatScale = seatscale ?: ""
            )
        } else {
            println("Failed to fetch XML data from the second API.")
        }
        return null
    }

//    private fun extractAndProcessDetailData(xmlData: String?): List<FacilityDetail> {
//        val facilityDetails = mutableListOf<FacilityDetail>()
//
//        if (xmlData != null) {
//            val doc: Document = Jsoup.parse(xmlData)
//            val prfstElements = doc.select("prfst")
//
//            for (prfstElement in prfstElements) {
//                val prfnmplc = prfstElement.selectFirst("prfnmplc")?.text()
//                val seatcnt = prfstElement.selectFirst("seatcnt")?.text()
//                val prfnmfct = prfstElement.selectFirst("prfnmfct")?.text()
//
//                if (!prfnmplc.isNullOrBlank() && !seatcnt.isNullOrBlank() && !prfnmfct.isNullOrBlank()) {
//                    facilityDetails.add(
//                        FacilityDetail(
//                            facilityDetailName = prfnmplc,
//                            seatCnt = seatcnt,
//                            facilityName = prfnmfct
//                        )
//                    )
//                }
//            }
//        } else {
//            println("Failed to fetch XML data from the second API.")
//        }
//
//        return facilityDetails
//    }
}