package com.jspl.tickettaka.infra.newPerformanceBatch

import com.jspl.tickettaka.model.Performance
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
class NewPerformanceBatchConfig(
    private val reader: NewPerformanceReader,
    private val writer: NewPerformanceWriter,
    private val processor: NewPerformanceProcessor
): DefaultBatchConfiguration() {
    @Bean
    fun settleFacilityDataJob(jobRepository: JobRepository): Job {
        return JobBuilder("settleFacilityDataJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(settleFacilityDataStep(jobRepository))
            .build()
    }

    @Bean
    fun settleFacilityDataStep(jobRepository: JobRepository): Step {
        return StepBuilder("settleFacilityDataStep", jobRepository)
            .allowStartIfComplete(true)
            .chunk<Performance, Performance>(1000, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build()
    }

    @Bean
    fun settleDataJobLauncher(): JobLauncher {
        val jobLauncher = TaskExecutorJobLauncher()
        jobLauncher.setJobRepository(jobRepository())
        jobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
        jobLauncher.afterPropertiesSet()
        return jobLauncher
    }

    @Scheduled(cron = "0 0 0 1 * *")
    fun recordJob() {
        settleDataJobLauncher().run(settleFacilityDataJob(jobRepository()), JobParameters())
    }
}