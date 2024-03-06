package com.cmcorg20230301.be.engine.knife4j.configuration;

import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public GroupedOpenApi groupedOpenApi(
        @Value("${spring.application.name:applicationName}") String applicationName) {

        return GroupedOpenApi.builder().group(applicationName)
            .addOpenApiMethodFilter(it -> it.getAnnotation(Operation.class) != null).build();

    }

}
