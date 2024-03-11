//package com.jspl.tickettaka.infra.batch
//
//import com.jspl.tickettaka.model.Performance
//import org.springframework.batch.core.Job
//import org.springframework.batch.core.JobParameters
//import org.springframework.batch.core.Step
//import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
//import org.springframework.batch.core.job.builder.JobBuilder
//import org.springframework.batch.core.launch.JobLauncher
//import org.springframework.batch.core.launch.support.RunIdIncrementer
//import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher
//import org.springframework.batch.core.repository.JobRepository
//import org.springframework.batch.core.step.builder.StepBuilder
//import org.springframework.context.annotation.Bean
//import org.springframework.core.task.SimpleAsyncTaskExecutor
//import org.springframework.scheduling.annotation.Scheduled
//
//class BatchConfig(
//    private val reader: PerformanceReader,
//    private val writer: PerformanceWriter,
//    private val processor: PerformanceProcessor
//): DefaultBatchConfiguration() {
//
//    @Bean
//    fun settlePerformanceJob(jobRepository: JobRepository): Job {
//        return JobBuilder("settlePerformanceJob", jobRepository)
//            .incrementer(RunIdIncrementer())
//            .start(settlePerformanceStep(jobRepository))
//            .build()
//    }
//
//    @Bean
//    fun settlePerformanceStep(jobRepository: JobRepository): Step {
//        return StepBuilder("settlePerformanceStep", jobRepository)
//            .allowStartIfComplete(true)
//            .chunk<Performance, Performance>(100, transactionManager)
//            .reader(reader)
//            .processor(processor)
//            .writer(writer)
//            .build()
//    }
//
//    @Bean
//    fun recordJobLauncher(): JobLauncher {
//        val jobLauncher = TaskExecutorJobLauncher()
//        jobLauncher.setJobRepository(jobRepository())
//        jobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
//        jobLauncher.afterPropertiesSet()
//        return jobLauncher
//    }
//
//    @Scheduled(cron = "0 0 0 1 * *")
//    fun recordJob() {
//        recordJobLauncher().run(settlePerformanceJob(jobRepository()), JobParameters())
//    }
//}