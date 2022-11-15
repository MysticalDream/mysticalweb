package nullpolltest;

import com.mysticaldream.glutemo.utils.ByteArrayList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author MysticalDream
 */
public class MyTest1 {
    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8666));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            System.out.println("selecting");
            int select = selector.select();
            System.out.println("selected:" + select);
            if (select > 0) {
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isAcceptable()) {
                        System.out.println("doAccept:" + selectionKey.channel());
                        doAccept(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        System.out.println("doRead:" + selectionKey.channel());
                        doRead(selectionKey);
                    }
                    keyIterator.remove();
                }
            }
        }

    }

    private static void doRead(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        ByteArrayList byteArrayList = ByteArrayList.getInstance();
        int read;
        while ((read = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            byteArrayList.addAll(buffer.array(), 0, read);
            buffer.clear();
        }
        System.out.println(new String(byteArrayList.values()));
        byteArrayList.clear();
        if (read == -1) {
            System.out.println(socketChannel.isOpen());
            System.out.println(socketChannel.socket().isInputShutdown());
            System.err.println("客户端关闭");
            System.exit(1);
        }
    }

    private static void doAccept(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel accept = serverSocketChannel.accept();
        System.out.println("accepted:" + accept);
        accept.configureBlocking(false);
        accept.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
}
