package com.bbubbush.batch.helloworld;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class HelloWorldJob {
  private final PlatformTransactionManager platformTransactionManager;
  private final JobRepository jobRepository;
  @Bean
  public Job helloJob() {
    return new JobBuilder("helloJob1", jobRepository)
      .incrementer(new RunIdIncrementer())
      .start(step())
      .build();
  }
  @Bean
  public Step step() {
    return new StepBuilder("step", jobRepository)
      .tasklet((contribution, chunkContext) -> {
        log.info("Hello, World!");
        return RepeatStatus.FINISHED;
      }, platformTransactionManager)
      .build();
  }


}
