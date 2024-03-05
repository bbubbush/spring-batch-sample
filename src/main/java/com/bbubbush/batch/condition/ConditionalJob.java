package com.bbubbush.batch.condition;

import java.util.Random;

import org.springframework.batch.core.ExitStatus;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConditionalJob {

	private final PlatformTransactionManager transactionManager;
	private final JobRepository jobRepository;

	@Bean
	public Job simpleConditionalJob() {
		return new JobBuilder("simpleConditionalJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(beginStep())
					.on("COMPLETED").to(successStep())
				.from(beginStep())
					.on("*").to(failStep())
				.next(secondStep())
				.end()
				.build();
	}

	@Bean
	public Step beginStep() {
		return new StepBuilder("beginStep", jobRepository).tasklet(((contribution, chunkContext) -> {
			ExitStatus status = new Random().nextBoolean() ? ExitStatus.COMPLETED : ExitStatus.FAILED;
			log.info("beginStep excute :: {}", status);
			// 성공과 실패의 값을 정함
			contribution.setExitStatus(status);
			return RepeatStatus.FINISHED;
		}), transactionManager).build();
	}

	@Bean
	public Step successStep() {
		return new StepBuilder("successStep", jobRepository).tasklet(((contribution, chunkContext) -> {
			log.info("		successStep excute");
			return RepeatStatus.FINISHED;
		}), transactionManager)
		.build();
	}

	@Bean
	public Step failStep() {
		return new StepBuilder("failStep", jobRepository).tasklet(((contribution, chunkContext) -> {
			log.info("		failStep excute");
			return RepeatStatus.FINISHED;
		}), transactionManager).build();
	}

	@Bean
	public Step secondStep() {
		return new StepBuilder("secondStep", jobRepository).tasklet(((contribution, chunkContext) -> {
			log.info("secondStep excute");
			return RepeatStatus.FINISHED;
		}), transactionManager).build();
	}
}
