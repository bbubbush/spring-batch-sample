package com.bbubbush.batch.http;

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

import com.bbubbush.batch.http.service.PetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class HttpJob {
  private final PlatformTransactionManager platformTransactionManager;
  private final JobRepository jobRepository;
  private final PetService catService;

  @Bean
  public Job simpleCatJob() {
    return new JobBuilder("simpleCatJob", jobRepository)
      .incrementer(new RunIdIncrementer())
      .start(getRandomImageStep())
      .build();
  }

  @Bean
  public Step getRandomImageStep() {
    return new StepBuilder("getRandomImageStep", jobRepository)
      .tasklet((contribution, chunkContext) -> {
    	  catService.getRandomImage();
        return RepeatStatus.FINISHED;
      }, platformTransactionManager)
      .build();
  }

  @Bean
  public Step catStep() {
	  return new StepBuilder("catStep", jobRepository)
			  .tasklet((contribution, chunkContext) -> {
				  if (!catService.downloadImage("https://cdn2.thecatapi.com/images/cid.jpg")) {
					  return RepeatStatus.CONTINUABLE;
				  }
				  return RepeatStatus.FINISHED;
			  }, platformTransactionManager)
			  .build();
  }
}
