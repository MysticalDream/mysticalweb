package buffertest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author MysticalDream
 */
public class BufferTest2 {

    @Test
    public void test() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(8080));
        ByteBuffer wrap = ByteBuffer.wrap("123".getBytes());
        System.out.println(wrap.position());
        socketChannel.write(wrap);
        System.out.println(wrap.position());
    }

}
