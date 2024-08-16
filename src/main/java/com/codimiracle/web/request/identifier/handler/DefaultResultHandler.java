package com.codimiracle.web.request.identifier.handler;

import com.codimiracle.web.request.identifier.exception.RepeatSubmissionException;

/**
 * A default implementation of {@link ResultHandler}
 * if the request id is duplicated, throw {@link RepeatSubmissionException}
 * otherwise do nothing
 *
 * @author Codimiracle
 * @since 0.0.1
 *
 * @see RepeatSubmissionException
 * @see ResultHandler
 */
public class DefaultResultHandler implements ResultHandler {
    @Override
    public Object onCheckedFailure(String requestId) throws Throwable {
        throw new RepeatSubmissionException("your request id is duplicated, given request id [" + requestId + "]");
    }
}
