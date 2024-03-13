package com.jspl.tickettaka.infra.createFacilityInstanceBatch

import com.jspl.tickettaka.model.FacilityDetail
import com.jspl.tickettaka.model.FacilityInstance
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
class CreateFacilityInsBatchConfig(
    private val reader: CreateFacilityInsReader,
    private val processor: CreateFacilityInsProcessor,
    private val writer: CreateFacilityInsWriter
): DefaultBatchConfiguration() {
    @Bean
    fun settleCreateJob(jobRepository: JobRepository): Job {
        return JobBuilder("settleCreateJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(settleCreateStep(jobRepository))
            .build()
    }

    @Bean
    fun settleCreateStep(jobRepository: JobRepository): Step {
        return StepBuilder("settleDataStep", jobRepository)
            .allowStartIfComplete(true)
            .chunk<FacilityDetail, List<FacilityInstance>>(1000, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build()
    }

    @Bean
    fun settleCreateJobLauncher(): JobLauncher {
        val jobLauncher = TaskExecutorJobLauncher()
        jobLauncher.setJobRepository(jobRepository())
        jobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
        jobLauncher.afterPropertiesSet()
        return jobLauncher
    }

    @Scheduled(cron = "0 0 0 1 * *")
    fun recordJob() {
        settleCreateJobLauncher().run(settleCreateJob(jobRepository()), JobParameters())
    }
}