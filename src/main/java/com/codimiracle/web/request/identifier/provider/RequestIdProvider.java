package com.codimiracle.web.request.identifier.provider;

import com.codimiracle.web.request.identifier.annotation.NonRepeatable;
import org.aspectj.lang.JoinPoint;

public interface RequestIdProvider {
    String toRequestId(NonRepeatable nonRepeatable, JoinPoint joinPoint);

    boolean isValidated(NonRepeatable nonRepeatable, JoinPoint joinPoint, String requestId);
}
