package com.codimiracle.web.request.identifier;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.codimiracle.web.request.identifier", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestApplication.ResultHandlerImpl.class, TestApplication.class, TestApplicationWithoutRedisProvider.class}))
public class TestApplicationWithoutResultHandler {
}
