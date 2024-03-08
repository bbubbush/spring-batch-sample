package com.bbubbush.batch.http;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
		.<String, String>chunk(10, platformTransactionManager)
		.reader(getRandomImage())
		.writer(downloadImage())
		.taskExecutor(taskExecutor())
		.build();
  }


  @Bean
  public ItemReader<String> getRandomImage() {
	  return new ListItemReader<String>(catService.getRandomImageUrls(10));
  }

  @Bean
  public ItemWriter<String> downloadImage() {
	  return urlList -> {
          for (String url : urlList) {
              catService.downloadImage(url);
          }
      };
  }

  @Bean
  public TaskExecutor taskExecutor() {
	  ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	  executor.setCorePoolSize(3);
	  executor.setMaxPoolSize(3);
	  executor.setThreadNamePrefix("parallel-");
      return executor;
  }

}
