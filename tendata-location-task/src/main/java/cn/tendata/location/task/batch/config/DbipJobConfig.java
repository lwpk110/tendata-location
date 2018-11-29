package cn.tendata.location.task.batch.config;

import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem;
import cn.tendata.location.data.elasticsearch.rest.repository.IpLocationRepository;
import cn.tendata.location.task.batch.GzipBufferedReaderFactory;
import cn.tendata.location.task.batch.item.DbipItemProcessor;
import cn.tendata.location.task.batch.item.IplocationItemWriter;
import cn.tendata.location.task.batch.item.SplitItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

import static cn.tendata.location.task.batch.JobParameters.INPUT_FILE_NAME;

@Configuration
@Import(IntegrationConfig.class)
public class DbipJobConfig {

    @Bean
    public LineMapper<FieldSet> defaultLineMapper() {
        DefaultLineMapper<FieldSet> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        return lineMapper;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> dbipFileReader(@Value("#{jobParameters['" + INPUT_FILE_NAME + "']}")
                                                               String pathToFile) {
        FlatFileItemReader<FieldSet> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(pathToFile));
        reader.setBufferedReaderFactory(new GzipBufferedReaderFactory());
        reader.setLineMapper(defaultLineMapper());
        return reader;
    }

    @Bean
    public ItemProcessor<FieldSet, List<IpLocationItem>> dbipItemProcessor() {
        return new DbipItemProcessor();
    }


    @Bean
    public ItemProcessor dbipAsyncItemProcessor() {
        AsyncItemProcessor<FieldSet, List<IpLocationItem>> dbipItemProcessor = new AsyncItemProcessor<>();
        dbipItemProcessor.setDelegate(dbipItemProcessor());
        dbipItemProcessor.setTaskExecutor(asyncProcessTaskExecutor());
        return dbipItemProcessor;
    }

    @Bean
    public ItemWriter<IpLocationItem> defaultItemWriter(IpLocationRepository repository) {
        return new IplocationItemWriter(repository);
    }

    @Bean
    public ItemWriter<List<IpLocationItem>> dbipItemWriter(ItemWriter<IpLocationItem> defaultItemWriter) {
        return new SplitItemWriter(defaultItemWriter);
    }

    @Bean
    public ItemWriter dbipAsyncItemWriter(
            @Qualifier("dbipItemWriter") ItemWriter<List<IpLocationItem>> dbipItemWriter) {
        AsyncItemWriter<List<IpLocationItem>> dbipAsyncItemWriter = new AsyncItemWriter<>();
        dbipAsyncItemWriter.setDelegate(dbipItemWriter);
        return dbipAsyncItemWriter;
    }

    @Bean
    public TaskExecutor asyncProcessTaskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(50);
        return taskExecutor;
    }

    @Bean
    public Step dbipStep0(StepBuilderFactory stepBuilderFactory,
                          PlatformTransactionManager transactionManager,
                          ItemReader<FieldSet> dbipReader,
                          @Qualifier("dbipAsyncItemWriter") ItemWriter<List<IpLocationItem>> dbipAsyncItemWriter,
                          @Qualifier("dbipAsyncItemProcessor") ItemProcessor<FieldSet, List<IpLocationItem>>
                                  dbipAsyncItemProcessor) {
        return stepBuilderFactory.get("dbipStep0")
                .transactionManager(transactionManager)
                .<FieldSet, List<IpLocationItem>>chunk(1000)
                .reader(dbipReader)
                .processor(dbipAsyncItemProcessor)
                .writer(dbipAsyncItemWriter)
                .build();
    }

    @Bean
    public Job dbipIpLocationDataStoreJob(JobBuilderFactory jobBuilderFactory,
                                          @Qualifier("notificationExecutionsListener") JobExecutionListener listener,
                                          @Qualifier("dbipStep0") Step dbipStep0) {
        return jobBuilderFactory.get("dbipIpLocationDataStoreJob")
                .listener(listener)
                .incrementer(new RunIdIncrementer())
                .flow(dbipStep0).end().build();
    }


}
