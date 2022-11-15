package com.mysticaldream.glutemo.utils;

import java.util.UUID;

/**
 * @author MysticalDream
 */
public final class UUIDUtils {


    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

}
