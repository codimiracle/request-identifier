package com.codimiracle.web.request.identifier.provider;

import com.codimiracle.web.request.identifier.annotation.NonRepeatable;
import com.codimiracle.web.request.identifier.enumeration.IdentifierStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class DefaultRequestIdProviderImpl implements RequestIdProvider {
    private String digestToRequestId(String signature, Object requestArg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = mapper.writeValueAsString(requestArg);
            return DigestUtils.sha1Hex(signature + content);
        } catch (JsonProcessingException e) {
            // enclosed.
        }
        return null;
    }

    private String generateRequestIdByArgs(JoinPoint joinPoint) {
        return digestToRequestId(joinPoint.getSignature().toString(), joinPoint.getArgs());
    }

    private String retrieveRequestIdByParameterName(JoinPoint joinPoint, String parameterName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (StringUtils.isEmpty(parameterName)) {
            // try best effort
            return digestToRequestId(joinPoint.getSignature().toString(), request.getParameterMap());
        }
        return digestToRequestId("", request.getParameter(parameterName));
    }

    @Override
    public String toRequestId(NonRepeatable nonRepeatable, JoinPoint joinPoint) {
        if (nonRepeatable.strategy() == IdentifierStrategy.ARGUMENTS) {
            return generateRequestIdByArgs(joinPoint);
        } else if (nonRepeatable.strategy() == IdentifierStrategy.REQUEST_PARAMETER) {
            return retrieveRequestIdByParameterName(joinPoint, nonRepeatable.parameterName());
        }
        return null;
    }

    @Override
    public boolean isValidated(NonRepeatable nonRepeatable, JoinPoint joinPoint, String requestId) {
        return true;
    }
}
