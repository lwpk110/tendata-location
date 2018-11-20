package cn.tendata.location.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "cn.tendata.location.**.web.controller")
public class WebMvcConfig implements WebMvcConfigurer {
}
