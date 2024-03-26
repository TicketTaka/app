package com.jspl.tickettaka.infra.batch

import com.jspl.tickettaka.data.PerformanceDataCrawling
import com.jspl.tickettaka.infra.batch.DataBatchConfig
import com.jspl.tickettaka.repository.FacilityDetailRepository
import com.jspl.tickettaka.repository.FacilityInstanceRepository
import com.jspl.tickettaka.repository.PerformanceInstanceRepository
import com.jspl.tickettaka.repository.PerformanceRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.transaction.PlatformTransactionManager


@SpringBootTest(classes = [BatchTestConfig::class, DataBatchConfig::class])
@SpringBatchTest
@ActiveProfiles("test")
//@ContextConfiguration(classes = [BatchTestConfig::class, DataBatchConfig::class])
//@ComponentScan(basePackages = ["com.jspl.tickettaka.infra.batch"])
//@ComponentScan("com.jspl.tickettaka.infra.batch.DataBatchConfig")
//@SpringJUnitConfig(classes = [BatchTestConfig::class])
//@Import(DataBatchConfig::class)
//@EnableAutoConfiguration
//@SpringJUnitConfig(classes = [BatchTestConfig::class, DataBatchConfig::class])
class BatchJobConfigTest(
//    @Autowired
//    private val jobLauncherTestUtils: JobLauncherTestUtils
) {
    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils
    @MockBean
    private lateinit var facilityInstanceRepository: FacilityInstanceRepository
    @MockBean
    private lateinit var facilityDetailRepository: FacilityDetailRepository
    @MockBean
    private lateinit var performanceInstanceRepository: PerformanceInstanceRepository
    @MockBean
    private lateinit var performanceRepository: PerformanceRepository
    @MockBean
    private lateinit var performanceDataCrawling: PerformanceDataCrawling
    @MockBean
    private lateinit var jobRepository: JobRepository
    @MockBean
    private lateinit var transactionManager: PlatformTransactionManager
//    private lateinit var jobRepositoryTestUtils: JobRepositoryTestUtils
    @Test
    fun success(
    ) {
//        facilityDataCrawling.fetchAndSaveFacilities()
//        performanceDataCrawling.execute("20240213", "20240313")

        val execution: JobExecution = jobLauncherTestUtils.launchJob()

        Assertions.assertEquals(execution.exitStatus, ExitStatus.COMPLETED)
    }

}