package com.codimiracle.web.request.identifier.provider;

public interface NonRepeatableProvider {
    boolean isRepeat(String requestId);

    boolean isRepeat(String requestId, long interval);
}
