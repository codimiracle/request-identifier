package com.codimiracle.web.request.identifier;

import com.codimiracle.web.request.identifier.handler.ResultHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.codimiracle.web.request.identifier", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestApplicationWithoutRedisProvider.class, TestApplicationWithoutResultHandler.class}))
public class TestApplication {
    @Bean
    public ResultHandler getBean() {
        return new ResultHandlerImpl();
    }

    public static class ResultHandlerImpl implements ResultHandler {
        @Override
        public Object onCheckedSuccess(String requestId, ProceedingJoinPoint joinPoint) throws Throwable {
            return joinPoint.proceed();
        }

        @Override
        public Object onCheckedFailure(String requestId) {
            return "repeated";
        }
    }
}
