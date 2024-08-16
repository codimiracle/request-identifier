package com.codimiracle.web.request.identifier.provider;

import com.codimiracle.web.request.identifier.annotation.NonRepeatable;
import org.aspectj.lang.JoinPoint;

/**
 * request id provider
 * generate request id and validate request id
 *
 * @author codimiracle
 * @since 0.0.1
 */
public interface RequestIdProvider {
    String toRequestId(NonRepeatable nonRepeatable, JoinPoint joinPoint);

    boolean isValidated(NonRepeatable nonRepeatable, JoinPoint joinPoint, String requestId);
}
