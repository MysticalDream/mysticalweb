package com.mysticaldream.minplte.http;

import javax.servlet.http.Cookie;
import java.util.Map;

/**
 * @author MysticalDream
 */
public class HttpResponseMessage {

    private String httpVersion;

    private int status;

    private String statusText;

    private Map<String, String> responseHeader;

    private Cookie[] cookies;

    private byte[] responseBody;


}
