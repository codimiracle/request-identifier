package com.codimiracle.web.request.identifier.handler;

import com.codimiracle.web.request.identifier.exception.RepeatSubmissionException;

public class DefaultResultHandler implements ResultHandler {
    @Override
    public Object onCheckedFailure(String requestId) throws Throwable {
        throw new RepeatSubmissionException("your request id is duplicated, given request id [" + requestId + "]");
    }
}
