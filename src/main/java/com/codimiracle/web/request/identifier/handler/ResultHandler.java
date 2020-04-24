package com.codimiracle.web.request.identifier.handler;

import org.aspectj.lang.ProceedingJoinPoint;

public interface ResultHandler {
    Object onCheckedSuccess(String requestId, ProceedingJoinPoint joinPoint) throws Throwable;

    Object onCheckedFailure(String requestId) throws Throwable;
}
