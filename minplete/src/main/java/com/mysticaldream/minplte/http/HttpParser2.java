package com.mysticaldream.minplte.http;


import com.mysticaldream.minplte.http.enums.RequestMethod;
import com.mysticaldream.minplte.http.exception.HttpParseException;
import com.mysticaldream.minplte.utils.ByteArrayList;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author MysticalDream
 */
public class HttpParser2 {

    private static final String CRLF = "\r\n";

    private static final byte[] CRLFBYTES = {'\r', '\n'};

    private static final byte[] DCRLFBYTES = {'\r', '\n', '\r', '\n'};

    private static final String BLANK = " ";

    private static final String REQUEST_SPLIT = "&";

    private static final String ASSIGN = "=";

    private static final String HEADER_SPLIT = ": ";

    private static final String COOKIE = "Cookie";

    private static final String COOKIE_SPLIT = "; ";

    private static final String CONTENT_LENGTH = "Content-Length";

    private final int NOT_PARSING = 0;

    private final int PARSING_REQUEST_LINE = 1;

    private final int PARSING_REQUEST_HEADS = 2;

    private final int PARSING_REQUEST_BODY = 3;

    private int status = NOT_PARSING;

    private HttpRequestMessage httpRequestMessage;

    private ByteArrayList requestLine;

    private ByteArrayList requestHeaders;

    private ByteArrayList requestBody;

    private ByteArrayList preBytes;

    private int contentLength;


    public HttpParser2() {
        preBytes = new ByteArrayList();
    }

    public HttpRequestMessage parse(byte[] data) {

        boolean flag;

        switch (status) {
            case NOT_PARSING:
                status = PARSING_REQUEST_LINE;
            case PARSING_REQUEST_LINE:
                if (preBytes.size() > 0) {
                    preBytes.addAll(data, 0, data.length);
                }
                data = preBytes.values();
                preBytes.clear();
                flag = parseRequestLine(data, 0, preBytes.size());
                break;
            case PARSING_REQUEST_HEADS:
                flag = parseRequestHeader(data, 0, data.length);
                break;
            case PARSING_REQUEST_BODY:
                flag = parseRequestBody(data, 0, data.length);
                break;
            default:
                throw new RuntimeException("未知状态");
        }
        if (flag) {
            return httpRequestMessage;
        } else {
            return null;
        }
    }

    private boolean parseRequestLine(byte[] data, int offset, int length) {
        //TODO 可以对参数范围检测
        if (requestLine == null) {
            requestLine = new ByteArrayList();
        }

        int index = indexOf(data, CRLFBYTES, offset, length);

        if (index != -1) {
            requestLine.addAll(data, offset, index);
            status = PARSING_REQUEST_HEADS;
            if (index + CRLFBYTES.length < length) {
                return parseRequestHeader(data, index + CRLFBYTES.length, length);
            } else {
                return false;
            }
        } else {
            requestLine.addAll(data, offset, length);
            return false;
        }

    }


    private boolean parseRequestHeader(byte[] data, int offset, int length) {
        if (requestHeaders == null) {
            requestHeaders = new ByteArrayList();
        }
        int index = indexOf(data, DCRLFBYTES, offset, length);
        if (index != -1) {
            requestHeaders.addAll(data, offset, index);
            createHttpRequestMessage();
            status = PARSING_REQUEST_BODY;
            if (index + DCRLFBYTES.length < length) {
                return parseRequestBody(data, index + DCRLFBYTES.length, length);
            } else {
                return false;
            }

        } else {
            requestHeaders.addAll(data, offset, length);
            return false;
        }
    }

    private boolean parseRequestBody(byte[] data, int offset, int length) {
        if (requestBody == null) {
            requestBody = new ByteArrayList();
        }

        if (contentLength == 0) {
            return true;
        }

        int requireLength = contentLength - requestBody.size();
        int haveLength = length - offset;

        if (haveLength >= requireLength) {
            requestBody.addAll(data, offset, requireLength);
            if (haveLength > requireLength) {
                preBytes.addAll(data, offset + requireLength, length);
            }
            httpRequestMessage.setRequestBody(requestBody.values());
            return true;
        } else {
            requestBody.addAll(data, offset, length);
            return false;
        }

    }

    private void createHttpRequestMessage() {
        try {
            HttpRequestMessage requestMessage = new HttpRequestMessage();

            parseRequestLine0(requestMessage);

            parseRequestHeaders0(requestMessage);

            httpRequestMessage = requestMessage;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseRequestHeaders0(HttpRequestMessage requestMessage) throws UnsupportedEncodingException {

        byte[] headers = requestHeaders.values();

        String decodeHeaders = URLDecoder.decode(new String(headers), "UTF8");

        String[] splitHeaders = decodeHeaders.split(CRLF);

        Map<String, String> headersMap = requestMessage.getRequestHeaders();

        for (int i = 0; i < splitHeaders.length; i++) {

            String s = splitHeaders[i];

            if ("".equals(s)) {
                break;
            }

            String[] split = s.split(HEADER_SPLIT);

            String key = split[0].trim();
            String value = split[1].trim();

            if (key.length() > 0 && value.length() > 0) {
                //统一小写
                headersMap.put(customization(key), value);
            }
        }

        String cookie = customization(COOKIE);

        if (headersMap.containsKey(cookie)) {
            parseCookie(headersMap.get(cookie), requestMessage);
        } else {
            requestMessage.setCookies(new Cookie[0]);
        }
    }

    private void parseCookie(String cookies, HttpRequestMessage requestMessage) {

    }

    public static String customization(String v) {
        return v.toLowerCase();
    }

    private void parseRequestLine0(HttpRequestMessage requestMessage) throws UnsupportedEncodingException {
        byte[] line = requestLine.values();
        String decodeLine = URLDecoder.decode(new String(line), "UTF-8");
        String[] splitLine = decodeLine.split(BLANK);

        if (splitLine.length != 3) {
            throw new HttpParseException("request line is irregular");
        }
        requestMessage.setRequestMethod(RequestMethod.valueOf(splitLine[0].toUpperCase()));
        requestMessage.setRequestURI(URI.create(splitLine[1]));
        requestMessage.setHttpVersion(splitLine[2]);
    }


    public int indexOf(byte[] b, byte[] target) {
        return indexOf(b, target, 0, b.length);
    }

    public int indexOf(byte[] b, byte[] target, int offset, int length) {

        if (b == null || b.length == 0 || target == null || target.length == 0 || ((offset | length | (offset + length) | (b.length - (offset + length))) < 0)) {
            return -1;
        }

        byte first = target[0];

        for (int i = offset, sourceLen = length; i < sourceLen; i++) {

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


    public void reset() {
        status = NOT_PARSING;
        httpRequestMessage = null;
        requestLine.clear();
        requestHeaders.clear();
        requestBody.clear();
        contentLength = 0;
    }

}
