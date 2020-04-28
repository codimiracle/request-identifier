package com.codimiracle.web.request.identifier.provider;
/*
 * MIT License
 *
 * Copyright (c) 2020 codimiracle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Using guava implements request unique checking.
 * note: this is not long term checking.
 */
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
