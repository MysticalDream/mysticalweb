import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.channel.NioReactor;
import com.mysticaldream.glutemo.channel.NioServerSocketChannel;
import com.mysticaldream.glutemo.channel.handler.ChannelHandlerContext;
import com.mysticaldream.glutemo.channel.handler.ChannelInHandler;
import com.mysticaldream.glutemo.pool.DefaultThreadFactory;
import com.mysticaldream.glutemo.pool.SimpleExecutor;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author MysticalDream
 */
public class PipelineTest {

    @Test
    public void test() throws IOException {


        AbstractNioChannel channel = new NioServerSocketChannel();

        channel.setReactor(new NioReactor(null, new SimpleExecutor(new DefaultThreadFactory("test"))));

        channel.pipeline().addLast("test", new ChannelInHandler() {
            @Override
            public void channelRegistered(ChannelHandlerContext context) throws Exception {

            }

            @Override
            public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
                System.out.println(Thread.currentThread().getName() + ":" + msg);
                context.propagateReadEvent(msg);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {

            }
        });

        channel.read();

    }

}
