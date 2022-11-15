package com.mysticaldream.minplte.http;



import com.mysticaldream.minplte.http.enums.RequestMethod;

import javax.servlet.http.Cookie;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MysticalDream
 */
public class HttpRequestMessage {


    private RequestMethod requestMethod;

    private URI requestURI;

    private String httpVersion;

    private Map<String, String> requestHeaders;

    private Cookie[] cookies;

    private byte[] requestBody;

    private Map<String, String[]> parameterMap;


    public HttpRequestMessage() {
        requestHeaders = new HashMap<>(16);
        parameterMap = new HashMap<>(16);
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public URI getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(URI requestURI) {
        this.requestURI = requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

}
