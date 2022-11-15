package utils;

import com.mysticaldream.glutemo.utils.ByteArrayList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author MysticalDream
 */
public class ByteArrayTest {

    @Test
    public void test() {

        ByteArrayList byteArrayList = ByteArrayList.getInstance(10);

        byteArrayList.addAll(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        byte[] bytes = byteArrayList.values();

        System.out.println(bytes.length);

        System.out.println(Arrays.toString(bytes));

    }

    @Test
    public void test2() {

        for (int i = 0; i < 100; i++) {
            final int index = i;
            new Thread(() -> {
                ByteArrayList instance1 = ByteArrayList.getInstance();
                instance1.addAll(new byte[]{6, 5, 4, 3, 2, 1});
                System.out.println(Thread.currentThread().getName() + ":" + Arrays.toString(instance1.values()));
                ByteArrayList instance2 = ByteArrayList.getInstance();
                instance2.addAll(new byte[]{(byte) (index + 1)});
                System.out.println(Thread.currentThread().getName() + ":" + Arrays.toString(instance1.values()));
            }).start();
        }

    }
}
