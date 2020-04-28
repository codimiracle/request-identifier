package com.codimiracle.web.request.identifier.enumeration;

public enum IdentifierStrategy {
    /**
     * using method arguments to generate request id
     */
    ARGUMENTS,
    /**
     * using request parameter(s) to generate request id
     */
    REQUEST_PARAMETER;
}
