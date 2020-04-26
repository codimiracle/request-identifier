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
import com.codimiracle.web.request.identifier.exception.RepeatSubmissionException;
import com.codimiracle.web.request.identifier.handler.ResultHandler;
import com.codimiracle.web.request.identifier.provider.NonRepeatableProvider;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * checking request repeat logic.
 * note: request argument must implemented Object#toString() represent that obj state.
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

    private String generateRequestIdByArgs(ProceedingJoinPoint joinPoint) {
        StringBuilder builder = new StringBuilder();
        builder.append(joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            builder.append(arg);
        }
        return DigestUtils.sha1Hex(builder.toString());
    }

    private String retrieveByParameterName(NonRepeatable nonRepeatable) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getParameter(nonRepeatable.parameterName());
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
        if (Objects.nonNull(resultHandler)) {
            return resultHandler.onCheckedSuccess(requestId, joinPoint);
        } else {
            return joinPoint.proceed();
        }
    }

    private Object checkingFailure(String requestId) throws Throwable {
        if (Objects.nonNull(resultHandler)) {
            return resultHandler.onCheckedFailure(requestId);
        } else {
            throw new RepeatSubmissionException("your request id is duplicated");
        }
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
            requestId = retrieveByParameterName(nonRepeatable);
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
