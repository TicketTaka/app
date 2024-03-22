package com.jspl.tickettaka.infra.batch

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
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled


@Configuration
@EnableBatchProcessing
class DataBatchConfig(
//    private val performanceRepository: PerformanceRepository
    private val performanceUpdateReader: PerformanceUpdateReader,
    private val performanceUpdateWriter: PerformanceUpdateWriter,
    private val performanceUpdateProcessor: PerformanceUpdateProcessor,
    private val newPerformanceReader: NewPerformanceReader,
    private val newPerformanceWriter: NewPerformanceWriter,
    private val newPerformanceProcessor: NewPerformanceProcessor,
    private val facilityInsReader: CreateFacilityInsReader,
    private val facilityInsProcessor: CreateFacilityInsProcessor,
    private val facilityInsWriter: CreateFacilityInsWriter,
    private val performanceInsReader: CreatePerformanceInsReader,
    private val performanceInsProcessor: CreatePerformanceInsProcessor,
    private val performanceInsWriter: CreatePerformanceInsWriter
): DefaultBatchConfiguration() {
    @Bean("settleDataJob")
    fun settleDataJob(
//        jobRepository: JobRepository,
    ): Job {
        return JobBuilder("settleDataJob", jobRepository())
            .incrementer(RunIdIncrementer())
            .start(updatePerformanceStep())
            .next(settleNewPerformanceStep())
            .next(settleCreateFacilityInsStep())
            .next(settleCreatePerformanceInsStep())
            .build()
    }

    @Bean("updatePerformanceStep")
    fun updatePerformanceStep(
//        jobRepository: JobRepository,
//        reader: PerformanceUpdateReader,
//        processor: PerformanceUpdateProcessor,
//        writer: PerformanceUpdateWriter
    ): Step {
        return StepBuilder("updatePerformanceStep", jobRepository())
            .allowStartIfComplete(true)
            .chunk<Performance, Performance>(1000, transactionManager)
            .reader(performanceUpdateReader)
            .processor(performanceUpdateProcessor)
            .writer(performanceUpdateWriter)
            .build()
    }

    @Bean("settleNewPerformanceStep")
    fun settleNewPerformanceStep(
//        jobRepository: JobRepository,
//        performanceDataCrawling: PerformanceDataCrawling,
//        reader: NewPerformanceReader,
//        processor: NewPerformanceProcessor,
//        writer: NewPerformanceWriter
    ): Step {
        return StepBuilder("settleNewPerformanceStep", jobRepository())
            .allowStartIfComplete(true)
            .chunk<Performance, Performance>(1000, transactionManager)
//            .reader(NewPerformanceReader(performanceDataCrawling, performanceRepository))
//            .processor(NewPerformanceProcessor(performanceRepository))
//            .writer(NewPerformanceWriter(performanceRepository))
            .reader(newPerformanceReader)
            .processor(newPerformanceProcessor)
            .writer(newPerformanceWriter)
            .build()
    }

    @Bean("settleCreateFacilityInsStep")
    fun settleCreateFacilityInsStep(
//        jobRepository: JobRepository,
//        facilityDetailRepository: FacilityDetailRepository,
//        facilityInstanceRepository: FacilityInstanceRepository,
//        reader: CreateFacilityInsReader,
//        processor: CreateFacilityInsProcessor,
//        writer: CreateFacilityInsWriter
    ): Step {
        return StepBuilder("settleCreateFacilityInsStep", jobRepository())
            .allowStartIfComplete(true)
            .chunk<FacilityDetail, List<FacilityInstance>>(1000, transactionManager)
//            .reader(CreateFacilityInsReader(facilityDetailRepository))
//            .processor(CreateFacilityInsProcessor())
//            .writer(CreateFacilityInsWriter(facilityInstanceRepository))
            .reader(facilityInsReader)
            .processor(facilityInsProcessor)
            .writer(facilityInsWriter)
            .build()
    }

    @Bean("settleCreatePerformanceInsStep")
    fun settleCreatePerformanceInsStep(
//        jobRepository: JobRepository,
//        performanceRepository: PerformanceRepository,
//        facilityDetailRepository: FacilityDetailRepository,
//        facilityInstanceRepository: FacilityInstanceRepository,
//        performanceInstanceRepository: PerformanceInstanceRepository
//        reader: CreatePerformanceInsReader,
//        processor: CreatePerformanceInsProcessor,
//        writer: CreatePerformanceInsWriter
    ): Step {
        return StepBuilder("settleCreatePerformanceInsStep", jobRepository())
            .allowStartIfComplete(true)
            .chunk<Performance, MutableList<Pair<FacilityInstance, PerformanceInstance>>>(1000, transactionManager)
//            .reader(CreatePerformanceInsReader(performanceRepository))
//            .processor(CreatePerformanceInsProcessor(facilityDetailRepository, facilityInstanceRepository))
//            .writer(CreatePerformanceInsWriter(performanceInstanceRepository))
            .reader(performanceInsReader)
            .processor(performanceInsProcessor)
            .writer(performanceInsWriter)
            .build()
    }

    @Bean("recordJobLauncher")
    fun recordJobLauncher(
//        jobRepository: JobRepository
    ): JobLauncher {
        val jobLauncher = TaskExecutorJobLauncher()
        jobLauncher.setJobRepository(jobRepository())
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