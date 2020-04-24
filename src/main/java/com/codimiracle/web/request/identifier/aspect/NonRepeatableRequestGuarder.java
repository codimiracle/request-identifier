package com.codimiracle.web.request.identifier.aspect;

import com.codimiracle.web.request.identifier.annotation.NonRepeatable;
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

import java.util.Objects;

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

    @Around(value = "nonRepeatable(nonRepeatable)")
    public Object checking(ProceedingJoinPoint joinPoint, NonRepeatable nonRepeatable) throws Throwable {
        if (Objects.isNull(this.nonRepeatableProvider)) {
            log.warn("NonRepeatableProvider bean is not found, skip repeat check.");
            return joinPoint.proceed();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            builder.append(arg);
        }
        String requestId = DigestUtils.sha1Hex(builder.toString());
        synchronized (lock.intern(requestId)) {
            boolean result;
            if (nonRepeatable.interval() == NonRepeatable.DEFAULT_INTERVAL) {
                result = nonRepeatableProvider.isRepeat(requestId, 1000);
            } else if (nonRepeatable.interval() > NonRepeatable.DEFAULT_INTERVAL) {
                result = nonRepeatableProvider.isRepeat(requestId, nonRepeatable.interval());
            } else {
                result = nonRepeatableProvider.isRepeat(requestId);
            }
            if (!result) {
                if (Objects.nonNull(resultHandler)) {
                    return resultHandler.onCheckedSuccess(requestId, joinPoint);
                } else {
                    return joinPoint.proceed();
                }
            } else {
                if (Objects.nonNull(resultHandler)) {
                    return resultHandler.onCheckedFailure(requestId);
                } else {
                    throw new RepeatSubmissionException("your request id is duplicated");
                }
            }
        }
    }
}
