package cn.tendata.location.server;

import cn.tendata.location.service.EntityService;
import cn.tendata.location.task.batch.config.DbipJobConfig;
import fr.pilato.spring.elasticsearch.ElasticsearchRestClientFactoryBean;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

/*    @Bean
    public TaskEventListener taskEventListener() {
        return new TaskEventListener();
    }*/

    @Configuration
    @ComponentScan(basePackageClasses = {EntityService.class})
    static class ServiceConfig{}


    @Configuration
    @EnableBatchProcessing
    @EnableBatchIntegration
    @Import({DbipJobConfig.class})
    static class BatchConfig {}

    @Configuration
    @ComponentScan(basePackages = {"cn.tendata.location.data.elasticsearch.rest.repository"})
    static class ElasticsearchConfig {

        @Bean
        public ElasticsearchRestClientFactoryBean restClientFactoryBean(@Value("${spring.elasticsearch.rest.uris}")
                                                                                String[] hosts) {
            ElasticsearchRestClientFactoryBean restClientFactoryBean = new ElasticsearchRestClientFactoryBean();
            restClientFactoryBean.setClasspathRoot("/elasticsearch/client/rest");
            restClientFactoryBean.setEsNodes(hosts);
            restClientFactoryBean.setAutoscan(true);
            restClientFactoryBean.setMergeMapping(true);
            return restClientFactoryBean;
        }
    }
}
