package com.bbubbush.batch.helloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HelloWorldJob {
  @Autowired
  private PlatformTransactionManager platformTransactionManager;
  @Autowired
  private JobRepository jobRepository;

  @Bean
  public Step step() {
    return new StepBuilder("step", jobRepository)
      .tasklet((contribution, chunkContext) -> {
        System.out.println("Hello, World!");
        return RepeatStatus.FINISHED;
      }, platformTransactionManager)
      .build();
  }

  @Bean
  public Job helloJob() {
    return new JobBuilder("helloJob", jobRepository)
      .start(step())
      .build();
  }

}
