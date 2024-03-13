package com.jspl.tickettaka.infra.createPerformanceInstanceBatch

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
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableBatchProcessing
class CreatePerformanceInsBatchConfig(
    private val reader: CreatePerformanceInsReader,
    private val processor: CreatePerformanceInsProcessor,
    private val writer: CreatePerformanceInsWriter
): DefaultBatchConfiguration() {
    @Bean
    fun settlePerformanceDataJob(jobRepository: JobRepository): Job {
        return JobBuilder("settlePerformanceDataJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(settlePerformanceDataStep(jobRepository))
            .build()
    }

    @Bean
    fun settlePerformanceDataStep(jobRepository: JobRepository): Step {
        return StepBuilder("settleFacilityDataStep", jobRepository)
            .allowStartIfComplete(true)
            .chunk<Performance, MutableList<Pair<FacilityInstance, PerformanceInstance>>>(1000, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build()
    }

    @Bean
    fun settlePerformanceDataJobLauncher(): JobLauncher {
        val jobLauncher = TaskExecutorJobLauncher()
        jobLauncher.setJobRepository(jobRepository())
        jobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
        jobLauncher.afterPropertiesSet()
        return jobLauncher
    }

    @Scheduled(cron = "0 10 0 1 * *")
    fun recordJob() {
        settlePerformanceDataJobLauncher().run(settlePerformanceDataJob(jobRepository()), JobParameters())
    }
}