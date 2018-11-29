package cn.tendata.location.task.batch.config;

import cn.tendata.location.data.elasticsearch.rest.repository.IpLocationRepository;
import cn.tendata.location.task.batch.ExceptionDataQueryParams;
import cn.tendata.location.task.batch.ExceptionDataQueryParams.Param;
import cn.tendata.location.task.integration.FileMessageToJobRequest;
import cn.tendata.location.task.integration.MessageToFileTransformer;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.messaging.MessageHandler;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static cn.tendata.location.task.batch.JobParameters.INPUT_FILE_NAME;

@Configuration
@IntegrationComponentScan
public class IntegrationConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String FILE_BACK_CHANNEL_NAME = "fileBackChannel";

    @Bean
    public DirectChannel fileBackChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel dbipJobCompletedChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel deleteExceptionDataRequestChannel() {
        return new DirectChannel();
    }

    @MessagingGateway(name = "notificationExecutionsListener", defaultRequestChannel = "dbipJobCompletedChannel")
    @SuppressWarnings("unused")
    public interface NotificationExecutionListener extends JobExecutionListener {
        @Gateway
        @Override
        void afterJob(@Nonnull JobExecution jobExecution);
    }

    @MessagingGateway(name = "excludeExceptionLocationDataRequestGateway",
            defaultRequestChannel = "deleteExceptionDataRequestChannel")
    @SuppressWarnings("unused")
    public interface ExcludeExceptionLocationDataRequestGateway {
        @Gateway
        void exclude(@Nonnull Param param);
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
    public IntegrationFlow backFileFlow(ExcludeExceptionLocationDataRequestGateway exceptionLocationDataRequestGateway,
                                        ExceptionDataQueryParams exceptionDataParams) {

        return flow -> flow.channel(FILE_BACK_CHANNEL_NAME)
                .wireTap(removeExceptionDataFlow(exceptionDataParams, exceptionLocationDataRequestGateway))
                .log(LoggingHandler.Level.INFO, "--> back file Message...")
                .transform(messageToFileTransformer())
                .handle(Files.outboundAdapter("headers.back_dir")
                        .deleteSourceFiles(true)
                        .autoCreateDirectory(true));

    }

    private IntegrationFlow removeExceptionDataFlow(ExceptionDataQueryParams exceptionDataQueryParams,
                                                    ExcludeExceptionLocationDataRequestGateway
                                                            exceptionLocationDataRequestGateway) {
        final List<Param> exceptionDataParamList = exceptionDataQueryParams.getExceptionDataParams();
        return flow -> flow.log("==> 移除例外数据...").filter(source -> CollectionUtils.isNotEmpty(exceptionDataParamList))
                .transform(message -> exceptionDataParamList)
                .split()
                .handle(message -> {
                    Param param = (Param) message.getPayload();
                    exceptionLocationDataRequestGateway.exclude(param);
                });
    }

    @Bean
    public IntegrationFlow doRemoveExceptionDataFlow(IpLocationRepository ipLocationRepository) {
        return flow -> flow.channel("deleteExceptionDataRequestChannel")
                .log("do remove exception data...")
                .handle((MessageHandler) message -> {
            Param param = (Param) message.getPayload();
            try {
                ipLocationRepository.refresh();
                ipLocationRepository.deleteByStartIpAndEndIp(param.getIpStart(), param.getIpEnd());
            } catch (IOException e) {
                logger.warn("do Remove Exception Data Flow occur en err", e);
            }
        });
    }

}
