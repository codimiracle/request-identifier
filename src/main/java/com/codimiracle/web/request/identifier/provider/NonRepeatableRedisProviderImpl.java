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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * Using redis implements request unique checking
 */
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
