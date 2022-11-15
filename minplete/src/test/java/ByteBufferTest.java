import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @author MysticalDream
 */
public class ByteBufferTest {

    @Test
    public void test() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 1, 4);
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        System.out.println(buffer.get());


    }

}
