package com.codimiracle.web.request.identifier.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@ConditionalOnClass(name = "org.springframework.data.redis.core.StringRedisTemplate")
public class NonRepeatableRedisProviderImpl implements NonRepeatableProvider {
    public static final String NON_REPEATABLE_KEY_ONCE = "nr:hll:consumed-request-ids";
    public static final String NON_REPEATABLE_KEY = "nr:consumed:request-id:%s";
    public static final String IS_REPEAT = "is-repeat";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean isRepeat(String requestId) {
        return redisTemplate.opsForHyperLogLog().add(NON_REPEATABLE_KEY_ONCE, requestId) == 0;
    }

    @Override
    public boolean isRepeat(String requestId, long interval) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(String.format(NON_REPEATABLE_KEY, requestId), IS_REPEAT, Duration.ofMillis(interval));
        return Objects.nonNull(result) && !result;
    }
}
