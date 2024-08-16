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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * an implementation of {@link NonRepeatableProvider} by java code
 * it is not recommended to use this implementation in production environment.
 * it will boom! of OOM
 *
 * @author codimiracle
 * @since 0.0.1
 * @see NonRepeatableProvider
 */
public class NonRepeatableJavaProviderImpl implements NonRepeatableProvider {
    private Map<String, String> cache = new ConcurrentHashMap<>();
    private Map<String, Long> cacheInterval = new ConcurrentHashMap<>();

    @Override
    public boolean isRepeat(String requestId) {
        String exists = cache.get(requestId);
        if (exists == null) {
            cache.put(requestId, requestId);
            return false;
        }
        return true;
    }

    @Override
    public boolean isRepeat(String requestId, long interval) {
        Long expires = cacheInterval.get(requestId);
        if (expires == null) {
            cacheInterval.put(requestId, System.currentTimeMillis() + interval);
            return false;
        }
        return expires > System.currentTimeMillis();
    }
}
