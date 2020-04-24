package com.codimiracle.web.request.identifier.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnMissingBean(NonRepeatableRedisProviderImpl.class)
public class NonRepeatableJavaProviderImpl implements NonRepeatableProvider {
    private Cache<String, String> cache = CacheBuilder.newBuilder().build();
    private Cache<String, Long> cacheInterval = CacheBuilder.newBuilder().build();

    @Override
    public boolean isRepeat(String requestId) {
        String exists = cache.getIfPresent(requestId);
        if (Objects.isNull(exists)) {
            cache.put(requestId, requestId);
            return false;
        }
        return true;
    }

    @Override
    public boolean isRepeat(String requestId, long interval) {
        Long expires = cacheInterval.getIfPresent(requestId);
        if (Objects.isNull(expires)) {
            cacheInterval.put(requestId, System.currentTimeMillis() + interval);
            return false;
        }
        return expires > System.currentTimeMillis();
    }
}
