package com.codimiracle.web.request.identifier;

import com.codimiracle.web.request.identifier.provider.NonRepeatableJavaProviderImpl;
import com.codimiracle.web.request.identifier.provider.NonRepeatableProvider;
import com.codimiracle.web.request.identifier.provider.NonRepeatableRedisProviderImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@ComponentScan(basePackages = "com.codimiracle.web.request.identifier", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestApplication.class, TestApplicationWithoutResultHandler.class}))
public class TestApplicationWithoutRedisProvider {

    @Bean
    @Primary
    public NonRepeatableProvider getProvider() {
        return new NonRepeatableJavaProviderImpl();
    }
}
