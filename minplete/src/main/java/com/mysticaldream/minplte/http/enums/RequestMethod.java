package com.mysticaldream.minplte.http.enums;

/**
 *
 * @author MysticalDream
 */
public enum RequestMethod {

    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), TRACE("TRACE");

    private String value;



    RequestMethod(String value) {
        this.value = value;
    }
}
