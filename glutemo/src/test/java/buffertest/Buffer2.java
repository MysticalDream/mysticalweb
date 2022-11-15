package buffertest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author MysticalDream
 */
public class Buffer2 {


    @Test
    public void test() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0});
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());


    }
}
