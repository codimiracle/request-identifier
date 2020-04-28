package com.codimiracle.web.request.identifier;

import com.codimiracle.web.request.identifier.handler.ResultHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@ComponentScan(basePackages = "com.codimiracle.web.request.identifier", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestApplicationWithoutRedisProvider.class, TestApplicationWithoutResultHandler.class}))
public class TestApplication {
    @Bean
    @Primary
    public ResultHandler getBean() {
        return new ResultHandlerImpl();
    }

    public static class ResultHandlerImpl implements ResultHandler {
        @Override
        public Object onCheckedFailure(String requestId) {
            return "repeated";
        }
    }
}
