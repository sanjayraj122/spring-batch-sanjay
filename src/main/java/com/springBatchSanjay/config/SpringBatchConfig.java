package com.springBatchSanjay.config;

import com.springBatchSanjay.entity.User;
import com.springBatchSanjay.processor.CustomItemProcessor;
import com.springBatchSanjay.repository.UserRepository;
import com.springBatchSanjay.writer.CustomItemWriter;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class SpringBatchConfig {
    private CustomItemWriter customItemWriter;
    private CustomItemProcessor customItemProcessor;

    private UserRepository userRepository;
    @Bean
    public FlatFileItemReader<User> reader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userReader")
                .resource(new ClassPathResource("people-1000.csv"))

                .linesToSkip(1)
                .lineMapper(lineMapper())
                .targetType(User.class)
                .build();
    }

    @Bean
    public LineMapper<User> lineMapper() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        // Remove "id" from the names so it is not set from CSV
        lineTokenizer.setNames("userId", "firstName", "lastName", "gender", "email", "phone", "dateOfBirth", "jobTitle");


        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

//    @Bean
//    public ItemWriter<User> writer(){
//        return new CustomItemWriter();
//    }

    @Bean
    RepositoryItemWriter<User> writer() {
        RepositoryItemWriter<User> writer = new RepositoryItemWriter<>();
        writer.setRepository(userRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public ItemProcessor<User,User> processor(){
        return new CustomItemProcessor();
    }
    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("csv-import-step",jobRepository)
                .<User,User>chunk(10,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    @Bean
    public Job job(JobRepository jobRepository,Step step){

        return new JobBuilder("importPerson-csv",jobRepository)
                .start(step)
                .build();
    }
}
