package cn.tendata.location.server;

import cn.tendata.location.integration.batch.config.IntegrationConfig;
import cn.tendata.location.service.EntityService;
import fr.pilato.spring.elasticsearch.ElasticsearchRestClientFactoryBean;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @ComponentScan(basePackageClasses = {EntityService.class})
    static class ServiceConfig {
    }

    @Configuration
    @Import({IntegrationConfig.class})
    static class BatchConfig {
    }

    @Configuration
    @ComponentScan(basePackages = {"cn.tendata.location.data.elasticsearch.rest.repository"})
    static class ElasticsearchConfig {

        @Bean
        public RestHighLevelClient restHighLevelClient(
                @Value("${spring.elasticsearch.rest.uris}") String[] hosts) {
            final HttpHost[] httpHosts = Arrays.stream(hosts)
                    .map(host -> HttpHost.create("http://" + host))
                    .toArray(HttpHost[]::new);
            RestClient restClient = RestClient.builder(httpHosts).build();
            return new RestHighLevelClient(restClient);
        }

        @Bean
        public ElasticsearchRestClientFactoryBean restClientFactoryBean(
                @Value("${spring.elasticsearch.rest.uris}") String[] hosts) {
            ElasticsearchRestClientFactoryBean restClientFactoryBean = new ElasticsearchRestClientFactoryBean();
            restClientFactoryBean.setClasspathRoot("/elasticsearch/client/rest");
            restClientFactoryBean.setEsNodes(hosts);
            restClientFactoryBean.setAutoscan(true);
            restClientFactoryBean.setMergeMapping(true);
            return restClientFactoryBean;
        }
    }
}
