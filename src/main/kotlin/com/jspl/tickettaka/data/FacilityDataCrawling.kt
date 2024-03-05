package com.jspl.tickettaka.data

import com.jspl.tickettaka.model.Facility
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.FacilityInstance
import com.jspl.tickettaka.repository.FacilityDetailRepository
import com.jspl.tickettaka.repository.FacilityInstanceRepository
import com.jspl.tickettaka.repository.FacilityRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.LocalDate

@Component
class FacilityDataCrawling(
    private val facilityRepository: FacilityRepository,
    private val facilityDetailRepository: FacilityDetailRepository,
    private val facilityInstanceRepository: FacilityInstanceRepository,

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
        )

        val xmlData1 = sendGetRequest(firstApiUrl, params1)

        val allFacilities = mutableListOf<Facility>()

        if (xmlData1 != null) {
            val mt10idsWithLocation = parseXmlForMt10idAndLocation(xmlData1)

            mt10idsWithLocation.forEach { (mt10id, location1, location2) ->
                val secondApiUrl = "https://kopis.or.kr/openApi/restful/prfplc/$mt10id"
                val secondApiParams = mapOf("service" to serviceKey, "newsql" to "Y")
                val secondApiResponse = sendGetRequest(secondApiUrl, secondApiParams)

                extractAndProcessData(secondApiResponse, location1, location2)?.let { allFacilities.add(it) }
            }
        } else {
            println("Failed to fetch XML data from the first API.")
        }

        println(allFacilities.size)

        facilityRepository.saveAll(allFacilities)
    }

    @Transactional
    fun createConcertHall() {
        facilityRepository.findAll().forEach { facility ->
            val facilityHallNum = facility.detailCnt.toInt()
            val seatCnt = if (facilityHallNum != 0) facility.seatScale.toInt() / facilityHallNum else 0
            val halls = if (facilityHallNum != 1) (1..facilityHallNum).map { "${it}관" } else listOf("본관")

            val concertHalls = halls.map { hall ->
                FacilityDetail(hall, seatCnt.toString(), facility.name, facility.uniqueId)
            }

            facilityDetailRepository.saveAll(concertHalls)
        }
    }

    @Transactional
    fun createInstance() {
        val allFacilityDetail = facilityDetailRepository.findAll()
        var today: LocalDate = LocalDate.now()
        val endDate: LocalDate = today.plusMonths(1)
        while (today != endDate) {
            for (facilityDetail in allFacilityDetail) {
                val facilityInstance = FacilityInstance(facilityDetail, today)
                facilityInstanceRepository.save(facilityInstance)
            }
            today = today.plusDays(1)
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
            connection.connectTimeout = 50000
            connection.readTimeout = 50000
            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            response
        } catch (e: Exception) {
            println("Failed to send HTTP request: ${e.message}")
            null
        }
    }

    private fun parseXmlForMt10idAndLocation(xmlData: String): List<Triple<String, String, String>> {
        val doc: Document = Jsoup.parse(xmlData)
        val mt10idsWithLocation: MutableList<Triple<String, String, String>> = mutableListOf()
        val elements: List<Element> = doc.select("mt10id")
        val location1: List<Element> = doc.select("sidonm")
        val location2: List<Element> = doc.select("gugunnm")

        for (i in elements.indices) {
            val mt10id = elements[i].text()
            val sidonm = if (i < location1.size) location1[i].text() else ""
            val gugunnm = if (i < location2.size) location2[i].text() else ""

            mt10idsWithLocation.add(Triple(mt10id, sidonm, gugunnm))
        }
        return mt10idsWithLocation
    }

    private fun extractAndProcessData(xmlData: String?, location1: String, location2: String): Facility? {
        if (xmlData != null) {
            val doc: Document = Jsoup.parse(xmlData)
            val fcltynm = doc.selectFirst("fcltynm")?.text()
            val mt10id = doc.selectFirst("mt10id")?.text()
            val mt13cnt = doc.selectFirst("mt13cnt")?.text()
            val fcltychartr = doc.selectFirst("fcltychartr")?.text()
            val adres = doc.selectFirst("adres")?.text()
            val seatscale = doc.selectFirst("seatscale")?.text()

            val pattern = "\\s*\\(.*?\\)".toRegex()
            val replaceName = fcltynm?.replace(pattern, "")

            return Facility(
                name = replaceName ?: "",
                uniqueId = mt10id ?: "",
                detailCnt = mt13cnt ?: "",
                character = fcltychartr ?: "",
                sido = location1,
                gugun = location2,
                locationDetail = adres ?: "",
                seatScale = seatscale ?: ""
            )
        } else {
            println("Failed to fetch XML data from the second API.")
        }
        return null
    }

}