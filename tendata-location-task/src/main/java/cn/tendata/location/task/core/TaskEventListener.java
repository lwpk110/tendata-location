/*
package cn.tendata.location.task.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.task.listener.annotation.AfterTask;
import org.springframework.cloud.task.listener.annotation.BeforeTask;
import org.springframework.cloud.task.listener.annotation.FailedTask;
import org.springframework.cloud.task.repository.TaskExecution;

public class TaskEventListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @BeforeTask
    public void before(TaskExecution taskExecution) {
        final String taskName = taskExecution.getTaskName();
        logger.info("==================>task start:{}", taskName);
    }

    @AfterTask
    public void after(TaskExecution taskExecution) {
        final String taskName = taskExecution.getTaskName();
        logger.info("==================>task end:{}", taskName);
    }
    @FailedTask
    public void fail(TaskExecution taskExecution, Throwable throwable) {
        final String taskName = taskExecution.getTaskName();
        final String errorMessage = taskExecution.getErrorMessage();
        logger.error("==================>task name:{}, fail:{}", taskName, errorMessage);
    }
}
*/
