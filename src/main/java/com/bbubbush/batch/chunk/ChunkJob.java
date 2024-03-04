package com.bbubbush.batch.chunk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ChunkJob {
  private final PlatformTransactionManager platformTransactionManager;
  private final JobRepository jobRepository;
  @Bean
  public Job simpleChunkJob() {
    return new JobBuilder("simpleChunkJob", jobRepository)
      .incrementer(new RunIdIncrementer())
      .start(simpleChunkStep())
      .build();
  }
  @Bean
  @StepScope
  public Step simpleChunkStep() {
    return new StepBuilder("simpleChunkStep", jobRepository)
      .<String, String>chunk(100, platformTransactionManager)
      .reader(itemReader())
      .processor(itemProcessor())
      .writer(itemWriter())
      .build();
  }

  @Bean
  @StepScope
  public ListItemReader<String> itemReader() {
    final List<String> items = new ArrayList<>();
    IntStream.range(0, 1000)
      .forEach(item -> items.add(String.valueOf(item)));
    return new ListItemReader<>(items);
  }

  @Bean
  @StepScope
  public ItemProcessor<String, String> itemProcessor() {
    return item -> {
        log.info("Current item :: {}", item);
        return item;
    };
  }

  @StepScope
  public ItemWriter<String> itemWriter() {
    return items -> {
      log.info("items size is {}", items.size());
    };

  }




}
