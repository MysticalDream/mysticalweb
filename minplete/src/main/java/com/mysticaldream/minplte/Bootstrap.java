package com.mysticaldream.minplte;

import com.mysticaldream.glutemo.bootstrap.ServerBootStrap;
import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.channel.NioReactorGroup;
import com.mysticaldream.glutemo.channel.NioServerSocketChannel;
import com.mysticaldream.glutemo.channel.handler.ChannelInitializeHandler;

/**
 * 启动类
 *
 * @author MysticalDream
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {

        NioReactorGroup main = new NioReactorGroup();

        NioReactorGroup sub = new NioReactorGroup();

        ServerBootStrap serverBootStrap = new ServerBootStrap(main, sub);

        serverBootStrap.addServerChannel(new NioServerSocketChannel(), 7425, new ChannelInitializeHandler<AbstractNioChannel>() {
            @Override
            public void initChannel(AbstractNioChannel channel) {
//                channel.pipeline().addLast(null);
            }
        });

        serverBootStrap.bind();
    }
}
