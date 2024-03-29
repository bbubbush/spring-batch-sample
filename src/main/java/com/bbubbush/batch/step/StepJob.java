package com.bbubbush.batch.step;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StepJob {
  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  @Bean
  public Job simpleStepJob() {
    return new JobBuilder("simpleStepJob", jobRepository)
      .start(step1())
      .listener(jobLisnerEvent())
      .next(step2())
      .build();
  }

  @Bean
  public Step step1() {
    return new StepBuilder("step1", jobRepository)
      .tasklet(((contribution, chunkContext) -> {
        log.info("==================");
        log.info("step1 was executed");
        log.info("==================");
        return RepeatStatus.FINISHED;
      }), transactionManager)
      .listener(stepLisnerEvent())
      .build();
  }

  @Bean
  public Step step2() {
    return new StepBuilder("step2", jobRepository)		// Job name
      .tasklet(((contribution, chunkContext) -> {
        log.info("==================");
        log.info("step2 was executed");
        log.info("==================");
        return RepeatStatus.FINISHED;
      }), transactionManager)
      .listener(stepLisnerEvent())
      .build();
  }

  private JobExecutionListener jobLisnerEvent() {
    return new JobExecutionListener() {
      @Override
      public void beforeJob(JobExecution jobExecution) {
        log.info("[Job 시작] " + jobExecution.getJobInstance().getJobName());
      }

      @Override
      public void afterJob(JobExecution jobExecution) {
        log.info("[Job 완료] " + jobExecution.getJobInstance().getJobName());
      }
    };
  }
  private StepExecutionListener stepLisnerEvent() {
    return new StepExecutionListener() {
      @Override
      public void beforeStep(StepExecution stepExecution) {
        log.info("[  Step 시작] " + stepExecution.getStepName());
      }
      @Override
      public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("[  Step 완료] " + stepExecution.getStepName());
        return StepExecutionListener.super.afterStep(stepExecution);
      }
    };
  }

}
