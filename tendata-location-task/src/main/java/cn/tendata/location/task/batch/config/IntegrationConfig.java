package cn.tendata.location.task.batch.config;

import cn.tendata.location.task.integration.FileMessageToJobRequest;
import cn.tendata.location.task.integration.MessageToFileTransformer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.handler.LoggingHandler;

import javax.annotation.Nonnull;
import java.io.File;

import static cn.tendata.location.task.batch.JobParameters.INPUT_FILE_NAME;

@Configuration
@IntegrationComponentScan
public class IntegrationConfig {
    private static final String FILE_BACK_CHANNEL_NAME = "fileBackChannel";

    @Bean
    public DirectChannel fileBackChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel dbipJobCompletedChannel() {
        return new DirectChannel();
    }


    @MessagingGateway(name = "notificationExecutionsListener", defaultRequestChannel = "dbipJobCompletedChannel")
    @SuppressWarnings("unused")
    public interface NotificationExecutionListener extends JobExecutionListener {
        @Gateway
        @Override
        void afterJob(@Nonnull JobExecution jobExecution);
    }

    @Bean
    public FileMessageToJobRequest dbipFileMessageToJobRequest(@Qualifier("dbipIpLocationDataStoreJob") Job dbipJob) {
        FileMessageToJobRequest fileMessageToJobRequest = new FileMessageToJobRequest();
        fileMessageToJobRequest.setFileParameterName(INPUT_FILE_NAME);
        fileMessageToJobRequest.setJob(dbipJob);
        return fileMessageToJobRequest;
    }

    @Bean
    public JobLaunchingGateway jobLaunchingGateway(JobRepository jobRepository) {
        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        simpleJobLauncher.setTaskExecutor(new SyncTaskExecutor());
        JobLaunchingGateway jobLaunchingGateway = new JobLaunchingGateway(simpleJobLauncher);
        jobLaunchingGateway.setOutputChannelName("nullChannel");
        return jobLaunchingGateway;
    }

    @Bean
    public IntegrationFlow integrationFlow(
            JobLaunchingGateway jobLaunchingGateway,
            @Qualifier("dbipFileMessageToJobRequest") FileMessageToJobRequest dbipFileMessageToJobRequest,
            @Value("${db-ip.data.input-directory}") String dbipFileInputDir) {
        return IntegrationFlows.from(Files.inboundAdapter(new File(dbipFileInputDir)).
                        filter(new SimplePatternFileListFilter("*.csv.gz")),
                c -> c.poller(Pollers.fixedRate(50000).maxMessagesPerPoll(1))).
                handle(dbipFileMessageToJobRequest).
                handle(jobLaunchingGateway).
                log(LoggingHandler.Level.INFO, "headers.id + ': ' + payload").
                get();
    }

    @Bean
    public MessageToFileTransformer messageToFileTransformer() {
        return new MessageToFileTransformer();
    }


    @Bean
    public IntegrationFlow jobCompleteFlow(@Value("${db-ip.data.success-directory}") String successBackDir,
                                           @Value("${db-ip.data.fail-directory}") String failBackDir) {
        return flow -> flow.channel("dbipJobCompletedChannel")
                .log("==>job status monitor.........")
                .routeToRecipients(recipientListRouterSpec ->
                    recipientListRouterSpec
                            .recipientFlow("payload.status.toString().equals('COMPLETED')",
                                    successFlow ->
                                            successFlow.enrichHeaders(headerEnricherSpec ->
                                                    headerEnricherSpec.header("back_dir", successBackDir))
                                                    .channel(FILE_BACK_CHANNEL_NAME))
                            .recipientFlow("payload.status.toString().equals('FAILED')",
                                    failFlow ->
                                            failFlow.enrichHeaders(headerEnricherSpec ->
                                                    headerEnricherSpec.header("back_dir", failBackDir))
                                                    .channel(FILE_BACK_CHANNEL_NAME))
                            .defaultOutputChannel("nullChannel")
                );
    }

    @Bean
    public IntegrationFlow backFileFlow() {
        return flow -> flow.channel(FILE_BACK_CHANNEL_NAME)
                .log(LoggingHandler.Level.INFO, "--> back file Message...")
                .transform(messageToFileTransformer())
                .handle(Files.outboundAdapter("headers.back_dir")
                        .deleteSourceFiles(true)
                        .autoCreateDirectory(true));
    }

}
