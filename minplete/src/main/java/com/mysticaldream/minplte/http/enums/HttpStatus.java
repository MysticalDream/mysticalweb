package com.mysticaldream.minplte.http.enums;

/**
 * @author MysticalDream
 */
public enum HttpStatus {

    OK(200),
    MOVED_TEMPORARILY(302),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);
    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
