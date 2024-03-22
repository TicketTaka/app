//package com.jspl.tickettaka.batch
//
//import com.jspl.tickettaka.infra.batch.DataBatchConfig
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.springframework.batch.core.ExitStatus
//import org.springframework.batch.core.Job
//import org.springframework.batch.core.JobExecution
//import org.springframework.batch.core.launch.JobLauncher
//import org.springframework.batch.core.repository.JobRepository
//import org.springframework.batch.test.JobLauncherTestUtils
//import org.springframework.batch.test.JobRepositoryTestUtils
//import org.springframework.batch.test.context.SpringBatchTest
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration
//import org.springframework.boot.context.properties.EnableConfigurationProperties
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
//
//
//@SpringBootTest
//@SpringBatchTest
//@ActiveProfiles("test")
////@ContextConfiguration(classes = [BatchTestConfig::class, DataBatchConfig::class])
//@ComponentScan("com.jspl.tickettaka.infra.batch.DataBatchConfig")
//@EnableAutoConfiguration
////@SpringJUnitConfig(classes = [BatchTestConfig::class, DataBatchConfig::class])
//class BatchJobConfigTest(
////    @Autowired
////    private val jobLauncherTestUtils: JobLauncherTestUtils
//) {
//
//    @Autowired
//    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils
////    @Autowired
////    private lateinit var jobRepositoryTestUtils: JobRepositoryTestUtils
//    @Test
//    fun success(
//        @Autowired
//        settleDataJob: Job
//    ) {
////        facilityDataCrawling.fetchAndSaveFacilities()
////        performanceDataCrawling.execute("20240213", "20240313")
//        jobLauncherTestUtils.job = settleDataJob
//
//        val execution: JobExecution = jobLauncherTestUtils.launchJob()
//
//        Assertions.assertEquals(execution.exitStatus, ExitStatus.COMPLETED)
//    }
//
//}