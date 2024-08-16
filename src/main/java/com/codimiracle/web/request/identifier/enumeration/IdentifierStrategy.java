package com.codimiracle.web.request.identifier.enumeration;

/**
 * request identifier strategy
 *
 * @author codimiracle
 * @since 0.0.1
 */
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
