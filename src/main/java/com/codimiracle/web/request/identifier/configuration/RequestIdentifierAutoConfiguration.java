package com.codimiracle.web.request.identifier.configuration;

import com.codimiracle.web.request.identifier.handler.DefaultResultHandler;
import com.codimiracle.web.request.identifier.handler.ResultHandler;
import com.codimiracle.web.request.identifier.provider.NonRepeatableJavaProviderImpl;
import com.codimiracle.web.request.identifier.provider.NonRepeatableProvider;
import com.codimiracle.web.request.identifier.provider.NonRepeatableRedisProviderImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RequestIdentifierAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(ResultHandler.class)
    public ResultHandler getDefaultHandler() {
        return new DefaultResultHandler();
    }

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    public NonRepeatableProvider getNonRepeatableProviderWithRedis() {
        return new NonRepeatableRedisProviderImpl();
    }

    @Bean
    @ConditionalOnMissingBean(NonRepeatableProvider.class)
    public NonRepeatableProvider getDefaultNonRepeatableProvider() {
        return new NonRepeatableJavaProviderImpl();
    }
}
