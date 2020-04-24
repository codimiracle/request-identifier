package com.codimiracle.web.request.identifier;

import com.codimiracle.web.request.identifier.provider.NonRepeatableRedisProviderImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.codimiracle.web.request.identifier", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {NonRepeatableRedisProviderImpl.class, TestApplication.class, TestApplicationWithoutResultHandler.class}))
public class TestApplicationWithoutRedisProvider {
}
