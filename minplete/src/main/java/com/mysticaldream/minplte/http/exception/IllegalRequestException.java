package com.mysticaldream.minplte.http.exception;

/**
 * @author MysticalDream
 */
public class IllegalRequestException extends RuntimeException {

    public IllegalRequestException(String message) {
        super(message);
    }

    public IllegalRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
