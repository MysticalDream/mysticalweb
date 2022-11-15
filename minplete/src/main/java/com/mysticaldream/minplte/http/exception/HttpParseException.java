package com.mysticaldream.minplte.http.exception;

/**
 * @author MysticalDream
 */
public class HttpParseException extends RuntimeException {
    public HttpParseException(String message) {
        super(message);
    }

    public HttpParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
