package com.kirtesh.subwaytransition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ConfigurationPropertiesScan("com.kirtesh.subwaytransition.config")
public class SubwayTransitionApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SubwayTransitionApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SubwayTransitionApplication.class);
    }
}
