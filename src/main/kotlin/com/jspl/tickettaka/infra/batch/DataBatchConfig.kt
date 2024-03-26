package com.jspl.tickettaka.infra.batch

import com.jspl.tickettaka.data.PerformanceDataCrawling
import com.jspl.tickettaka.infra.batch.createFacilityInstanceBatch.CreateFacilityInsProcessor
import com.jspl.tickettaka.infra.batch.createFacilityInstanceBatch.CreateFacilityInsReader
import com.jspl.tickettaka.infra.batch.createFacilityInstanceBatch.CreateFacilityInsWriter
import com.jspl.tickettaka.infra.batch.createPerformanceInstanceBatch.CreatePerformanceInsProcessor
import com.jspl.tickettaka.infra.batch.createPerformanceInstanceBatch.CreatePerformanceInsReader
import com.jspl.tickettaka.infra.batch.createPerformanceInstanceBatch.CreatePerformanceInsWriter
import com.jspl.tickettaka.infra.batch.newPerformanceBatch.NewPerformanceProcessor
import com.jspl.tickettaka.infra.batch.newPerformanceBatch.NewPerformanceReader
import com.jspl.tickettaka.infra.batch.newPerformanceBatch.NewPerformanceWriter
import com.jspl.tickettaka.infra.batch.updatePerformanceBatch.PerformanceUpdateProcessor
import com.jspl.tickettaka.infra.batch.updatePerformanceBatch.PerformanceUpdateReader
import com.jspl.tickettaka.infra.batch.updatePerformanceBatch.PerformanceUpdateWriter
import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.FacilityInstance
import com.jspl.tickettaka.model.Performance
import com.jspl.tickettaka.model.PerformanceInstance
import com.jspl.tickettaka.repository.FacilityDetailRepository
import com.jspl.tickettaka.repository.FacilityInstanceRepository
import com.jspl.tickettaka.repository.PerformanceInstanceRepository
import com.jspl.tickettaka.repository.PerformanceRepository
import lombok.AllArgsConstructor
import lombok.RequiredArgsConstructor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class DataBatchConfig(
    private val facilityInstanceRepository: FacilityInstanceRepository,
    private val facilityDetailRepository: FacilityDetailRepository,
    private val performanceDataCrawling: PerformanceDataCrawling,
    private val performanceRepository: PerformanceRepository,
    private val performanceInstanceRepository: PerformanceInstanceRepository,
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager


) {
    @Bean("settleDataJob")
    fun settleDataJob(
    ): Job {
        return JobBuilder("settleDataJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(updatePerformanceStep())
            .next(settleNewPerformanceStep())
            .next(settleCreateFacilityInsStep())
            .next(settleCreatePerformanceInsStep())
            .build()
    }

    @Bean("updatePerformanceStep")
    fun updatePerformanceStep(
    ): Step {
        return StepBuilder("updatePerformanceStep", jobRepository)
            .allowStartIfComplete(true)
            .chunk<Performance, Performance>(1000, transactionManager)
            .reader(PerformanceUpdateReader(performanceRepository))
            .processor(PerformanceUpdateProcessor())
            .writer(PerformanceUpdateWriter(performanceRepository))
            .build()
    }

    @Bean("settleNewPerformanceStep")
    fun settleNewPerformanceStep(
    ): Step {
        return StepBuilder("settleNewPerformanceStep", jobRepository)
            .allowStartIfComplete(true)
            .chunk<Performance, Performance>(1000, transactionManager)
            .reader(NewPerformanceReader(performanceDataCrawling, performanceRepository))
            .processor(NewPerformanceProcessor(performanceRepository))
            .writer(NewPerformanceWriter(performanceRepository))
            .build()
    }

    @Bean("settleCreateFacilityInsStep")
    fun settleCreateFacilityInsStep(
    ): Step {
        return StepBuilder("settleCreateFacilityInsStep", jobRepository)
            .allowStartIfComplete(true)
            .chunk<FacilityDetail, List<FacilityInstance>>(1000, transactionManager)
            .reader(CreateFacilityInsReader(facilityDetailRepository))
            .processor(CreateFacilityInsProcessor())
            .writer(CreateFacilityInsWriter(facilityInstanceRepository))
            .build()
    }

    @Bean("settleCreatePerformanceInsStep")
    fun settleCreatePerformanceInsStep(
    ): Step {
        return StepBuilder("settleCreatePerformanceInsStep", jobRepository)
            .allowStartIfComplete(true)
            .chunk<Performance, MutableList<Pair<FacilityInstance, PerformanceInstance>>>(1000, transactionManager)
            .reader(CreatePerformanceInsReader(performanceRepository))
            .processor(CreatePerformanceInsProcessor(facilityDetailRepository, facilityInstanceRepository))
            .writer(CreatePerformanceInsWriter(performanceInstanceRepository))
            .build()
    }

    @Bean("recordJobLauncher")
    fun recordJobLauncher(
    ): JobLauncher {
        val jobLauncher = TaskExecutorJobLauncher()
        jobLauncher.setJobRepository(jobRepository)
//        jobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
        jobLauncher.afterPropertiesSet()
        return jobLauncher
    }

//    @Scheduled(cron = "0 0 0 1 * *")
//    fun recordJob(
////        jobRepository: JobRepository,
//        settleDataJob: Job
//    ) {
//        recordJobLauncher().run(settleDataJob, JobParameters())
//    }
////    override fun getCharset(): Charset {
////        return StandardCharsets.ISO_8859_1
////    }
}