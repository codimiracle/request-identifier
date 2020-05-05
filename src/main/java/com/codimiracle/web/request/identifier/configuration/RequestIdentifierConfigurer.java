package com.codimiracle.web.request.identifier.configuration;

import com.codimiracle.web.request.identifier.aspect.NonRepeatableRequestGuarder;
import com.codimiracle.web.request.identifier.handler.DefaultResultHandler;
import com.codimiracle.web.request.identifier.handler.ResultHandler;
import com.codimiracle.web.request.identifier.provider.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RequestIdentifierConfigurer {
    @Bean
    @ConditionalOnMissingBean(ResultHandler.class)
    public ResultHandler getDefaultHandler() {
        return new DefaultResultHandler();
    }

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    public NonRepeatableProvider nonRepeatableRedisProvider() {
        return new NonRepeatableRedisProviderImpl();
    }

    @Bean
    @ConditionalOnMissingBean(RequestIdProvider.class)
    public RequestIdProvider defaultRequestIdProvider() {
        return new DefaultRequestIdProviderImpl();
    }

    @Bean
    @ConditionalOnMissingBean(NonRepeatableProvider.class)
    public NonRepeatableProvider defaultNonRepeatableProvider() {
        return new NonRepeatableJavaProviderImpl();
    }

    @Bean
    public NonRepeatableRequestGuarder nonRepeatableRequestGuarder() {
        return new NonRepeatableRequestGuarder();
    }
}
