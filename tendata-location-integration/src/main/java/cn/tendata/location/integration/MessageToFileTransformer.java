package cn.tendata.location.integration;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.integration.annotation.Transformer;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static cn.tendata.location.job.support.JobParameters.INPUT_FILE_NAME;

public class MessageToFileTransformer {

    @Transformer
    @SuppressWarnings("unused")
    public File toFile(JobExecution jobExecution) throws FileNotFoundException {
        JobParameters jobParameters = jobExecution.getJobParameters();
        String filePath = jobParameters.getString(INPUT_FILE_NAME);
        return ResourceUtils.getFile(filePath);
    }
}
