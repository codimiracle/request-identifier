package com.codimiracle.web.request.identifier.aspect;
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

import com.codimiracle.web.request.identifier.annotation.NonRepeatable;
import com.codimiracle.web.request.identifier.enumeration.IdentifierStrategy;
import com.codimiracle.web.request.identifier.handler.ResultHandler;
import com.codimiracle.web.request.identifier.provider.NonRepeatableProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * checking request repeat logic.
 *
 * @author Codimiracle
 */
@Slf4j
@Aspect
@Component
public class NonRepeatableRequestGuarder {
    Interner<String> lock = Interners.newWeakInterner();
    @Autowired(required = false)
    private NonRepeatableProvider nonRepeatableProvider;

    @Autowired(required = false)
    private ResultHandler resultHandler;

    @Pointcut(value = "@annotation(nonRepeatable)", argNames = "nonRepeatable")
    public void nonRepeatable(NonRepeatable nonRepeatable) {
    }

    private String digestToRequestId(String signature, Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = mapper.writeValueAsString(object);
            return DigestUtils.sha1Hex(signature + content);
        } catch (JsonProcessingException e) {
            // enclosed.
        }
        return null;
    }

    private String generateRequestIdByArgs(ProceedingJoinPoint joinPoint) {
        return digestToRequestId(joinPoint.getSignature().toString(), joinPoint.getArgs());
    }

    private String retrieveRequestIdByParameterName(ProceedingJoinPoint joinPoint, String parameterName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (StringUtils.isEmpty(parameterName)) {
            // try best effort
            return digestToRequestId(joinPoint.getSignature().toString(), request.getParameterMap());
        }
        return request.getParameter(parameterName);
    }

    private boolean isRepeat(String requestId, NonRepeatable nonRepeatable) {
        if (nonRepeatable.interval() == NonRepeatable.DEFAULT_INTERVAL) {
            return nonRepeatableProvider.isRepeat(requestId, 1000);
        } else if (nonRepeatable.interval() > NonRepeatable.DEFAULT_INTERVAL) {
            return nonRepeatableProvider.isRepeat(requestId, nonRepeatable.interval());
        } else {
            return nonRepeatableProvider.isRepeat(requestId);
        }
    }

    private Object checkingSuccess(String requestId, ProceedingJoinPoint joinPoint) throws Throwable {
        return resultHandler.onCheckedSuccess(requestId, joinPoint);
    }

    private Object checkingFailure(String requestId) throws Throwable {
        return resultHandler.onCheckedFailure(requestId);
    }

    @Around(value = "nonRepeatable(nonRepeatable)")
    public Object checking(ProceedingJoinPoint joinPoint, NonRepeatable nonRepeatable) throws Throwable {
        if (Objects.isNull(this.nonRepeatableProvider)) {
            log.warn("NonRepeatableProvider bean is not found, skip repeat check.");
            return joinPoint.proceed();
        }
        String requestId = null;
        if (nonRepeatable.strategy() == IdentifierStrategy.ARGUMENTS) {
            requestId = generateRequestIdByArgs(joinPoint);
        }
        if (nonRepeatable.strategy() == IdentifierStrategy.REQUEST_PARAMETER) {
            requestId = retrieveRequestIdByParameterName(joinPoint, nonRepeatable.parameterName());
        }
        if (Objects.isNull(requestId)) {
            log.warn("no request id generated, skip repeat check.");
            return joinPoint.proceed();
        }
        synchronized (lock.intern(requestId)) {
            if (!isRepeat(requestId, nonRepeatable)) {
                return checkingSuccess(requestId, joinPoint);
            } else {
                return checkingFailure(requestId);
            }
        }
    }
}
