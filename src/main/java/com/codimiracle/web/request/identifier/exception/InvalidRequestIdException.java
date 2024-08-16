package com.codimiracle.web.request.identifier.exception;

/**
 * Invalid request id exception
 * when request id is invalid, throwing this exception.
 *
 * @author Codimiracle
 * @since 0.0.1
 */
public class InvalidRequestIdException extends Throwable {
    public InvalidRequestIdException(String message) {
        super(message);
    }
}
