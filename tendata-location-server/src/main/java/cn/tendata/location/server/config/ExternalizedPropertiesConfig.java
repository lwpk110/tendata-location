package cn.tendata.location.server.config;

import cn.tendata.location.task.batch.ExceptionDataQueryParams;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalizedPropertiesConfig {

    @Bean
    @ConfigurationProperties(prefix = "db-ip.data")
    public ExceptionDataQueryParams exceptionDataParams(){
        return new ExceptionDataQueryParams();
    }
}
