package com.mysticaldream.minplte.http;

import com.mysticaldream.minplte.http.enums.RequestMethod;
import com.mysticaldream.minplte.http.exception.HttpParseException;
import com.mysticaldream.minplte.http.exception.IllegalRequestException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author MysticalDream
 */
@Slf4j
public class HttpParser {


    private static final String CRLF = "\r\n";

    private static final String DCRLF = "\r\n\r\n";

    private static final byte[] DCRLFBYTES = {'\r', '\n', '\r', '\n'};
    private static final String BLANK = " ";

    private static final String REQUEST_SPLIT = "&";

    private static final String ASSIGN = "=";

    private static final String HEADER_SPLIT = ": ";

    private static final String COOKIE = "Cookie";

    private static final String COOKIE_SPLIT = "; ";

    private static final String CONTENT_LENGTH = "Content-Length";

    enum SingletonHttpParser {
        /**
         * 单例
         */
        INSTANCE;
        HttpParser httpParser;

        SingletonHttpParser() {
            this.httpParser = new HttpParser();
        }
    }

    public static HttpParser getInstance() {
        return SingletonHttpParser.INSTANCE.httpParser;
    }


    public HttpRequestMessage parse(byte[] data) {

        if (log.isDebugEnabled()) {
            log.debug("{}:parsing", Thread.currentThread().getName());
        }

        if (data == null) {

            throw new HttpParseException("data can't be null");
        }

        if (data.length == 0) {
            log.error("空的请求");
            throw new IllegalRequestException("空的请求");
        }

        HttpRequestMessage message = new HttpRequestMessage();

        int DCRLFIndex = indexOf(data, DCRLFBYTES);

        String decode;
        try {
            decode = URLDecoder.decode(new String(data, 0, DCRLFIndex, StandardCharsets.UTF_8), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if (log.isDebugEnabled()) {
            log.debug("request info without body:\n{\n{}\n}", decode);
        }

        String[] requestMessage = decode.split(CRLF);

        //必须要包含请求行和请求头
        if (requestMessage.length <= 1) {
            throw new HttpParseException("Must include request line and request header");
        }

        parseRequestLine(requestMessage, message);

        parseRequestHeader(requestMessage, message);

        message.setRequestBody(Arrays.copyOfRange(data, DCRLFIndex + DCRLFBYTES.length, data.length));


//        parseRequestBody(data, DCRLFIndex + DCRLFBYTES.length, StandardCharsets.UTF_8, message);

        return message;
    }

    private void parseRequestHeader(String[] requestMessage, HttpRequestMessage message) {

        if (log.isDebugEnabled()) {
            log.debug("parsing request headers");
        }

        Map<String, String> requestHeaders = message.getRequestHeaders();

        for (int i = 1; i < requestMessage.length; i++) {

            String s = requestMessage[i];
            if ("".equals(s)) {
                break;
            }
            String[] split = s.split(HEADER_SPLIT);

            String key = split[0].trim();
            String value = split[1].trim();

            if (key.length() > 0 && value.length() > 0) {
                //统一小写
                requestHeaders.put(customization(key), value);
            }
        }

        String cookie = customization(COOKIE);

        if (requestHeaders.containsKey(cookie)) {
            parseCookie(requestHeaders.get(cookie), message);
            requestHeaders.remove(cookie);
        } else {
            message.setCookies(new Cookie[0]);
        }

    }

    private void parseCookie(String cookieStr, HttpRequestMessage message) {

        if (log.isDebugEnabled()) {
            log.debug("parse cookie");
        }

        List<Cookie> cookies = new ArrayList<>();


        if (cookieStr != null && cookieStr.trim().length() > 0) {
            String[] cookiesStr = cookieStr.split(COOKIE_SPLIT);

            for (int i = 0; i < cookiesStr.length; i++) {
                String[] split = cookiesStr[i].split(ASSIGN);
                if (!"".equals(split[0])) {
                    cookies.add(new Cookie(split[0], split[1]));
                }
            }
        }
        message.setCookies(cookies.toArray(new Cookie[0]));
    }

    public void parseRequestBody(byte[] bodyData, int offset, Charset charset, HttpRequestMessage message) {

        if (log.isDebugEnabled()) {
            log.debug("parsing request body");
        }

        String s = message.getRequestHeaders().get(customization(CONTENT_LENGTH));

        if (s != null) {
            int i = Integer.parseInt(s);
        }

        System.out.println(new String(bodyData, offset, bodyData.length - offset));

    }

    private void parseRequestLine(String[] requestMessage, HttpRequestMessage message) {

        if (log.isDebugEnabled()) {
            log.debug("parsing request line");
        }

        String line = requestMessage[0];

        String[] split = line.split(BLANK);

        if (split.length != 3) {
            throw new HttpParseException("request line is irregular");
        }

        // request method
        message.setRequestMethod(RequestMethod.valueOf(split[0].toUpperCase()));

        //request path

        message.setRequestURI(URI.create(split[1]));

        parseParams(message.getRequestURI(), message);

        //https version

        message.setHttpVersion(split[2]);


    }

    private void parseParams(URI uri, HttpRequestMessage message) {

        if (log.isDebugEnabled()) {
            log.debug("parse parameters");
        }

        Map<String, String[]> parameterMap = message.getParameterMap();

        String query = uri.getQuery();

        if (query == null) {
            return;
        }

        String[] split = query.split(REQUEST_SPLIT);

        for (String s : split) {
            String[] s1 = s.split(ASSIGN);
            String key = s1[0].trim();
            String[] value = {s1[1].trim()};
            if (key.length() > 0 && value[0].length() > 0) {
                String[] strings = parameterMap.get(key);
                if (strings == null) {
                    parameterMap.put(key, value);
                } else {
                    String[] all = new String[strings.length + 1];
                    System.arraycopy(strings, 0, all, 0, strings.length);
                    all[all.length - 1] = value[0];
                    parameterMap.put(key, all);
                }
            }
        }

    }


    private int indexOf(byte[] b, byte[] target) {

        if (b == null || b.length == 0 || target == null || target.length == 0) {
            return -1;
        }

        byte first = target[0];

        for (int i = 0, sourceLen = b.length; i < sourceLen; i++) {

            if (b[i] == first) {

                int sourceIndex = i + 1;

                int targetLen = target.length;

                int sourceEnd = sourceIndex + targetLen - 1;

                int targetIndex = 1;

                while (sourceIndex < sourceEnd && b[sourceIndex] == target[targetIndex]) {
                    targetIndex++;
                    sourceIndex++;
                }

                if (sourceIndex == sourceEnd) {
                    return i;
                }

            }
        }
        return -1;
    }

    public static String customization(String v) {
        return v.toLowerCase();
    }


}
