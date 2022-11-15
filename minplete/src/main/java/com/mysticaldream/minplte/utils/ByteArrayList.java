package com.mysticaldream.minplte.utils;

import java.util.Arrays;

/**
 * @author MysticalDream
 */
public class ByteArrayList {

    private byte[] data;

    private int size;

    private final int DEFAULT_CAPACITY = 100;

    private final byte[] NULL_BYTE_ARRAY = {};


    public ByteArrayList(int initialCapacity) {
        data = new byte[initialCapacity];
    }

    public ByteArrayList() {
        data = NULL_BYTE_ARRAY;
    }

    public void add(byte b) {
        checkBound();
        data[size++] = b;
    }


    public byte[] bytes() {
        return data;
    }


    public int size() {
        return size;
    }

    public byte[] values() {
        byte[] bytes = new byte[size];
        System.arraycopy(data, 0, bytes, 0, size);
        return bytes;
    }

    public void addAll(byte[] bytes) {
        addAll(bytes, 0, bytes.length);
    }

    public void addAll(byte[] bytes, int offset, int length) {

        if ((offset | length | (offset + length) | (bytes.length - (offset + length))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = offset; i < length; i++) {
            add(bytes[i]);
        }
    }

    /**
     * 用完记得清理
     */
    public void clear() {
        size = 0;
        data = null;
    }


    /**
     * 检查边界
     */
    private void checkBound() {
        int needCapacity;

        if (NULL_BYTE_ARRAY == data) {
            needCapacity = DEFAULT_CAPACITY;
        } else {
            needCapacity = size + 1;
        }
        grow(needCapacity);
    }

    private void grow(int needCapacity) {
        if (data.length - needCapacity < 0) {
            int oldCapacity = data.length;
            int newCapacity = oldCapacity + (oldCapacity >>> 1);
            if (newCapacity == 0) {
                newCapacity = needCapacity;
            }
            data = Arrays.copyOf(data, newCapacity);
        }
    }


}
