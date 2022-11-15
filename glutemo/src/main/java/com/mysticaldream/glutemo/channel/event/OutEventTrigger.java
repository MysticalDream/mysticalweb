package com.mysticaldream.glutemo.channel.event;

import com.mysticaldream.glutemo.promise.ChannelPromise;

/**
 * @author MysticalDream
 */
public interface OutEventTrigger {

    ChannelPromise write(Object msg, boolean flush);

    OutEventTrigger flush();

    ChannelPromise write(Object msg);

    ChannelPromise write(Object msg, ChannelPromise promise);

    ChannelPromise writeAndFlush(Object msg);

    ChannelPromise writeAndFlush(Object msg, ChannelPromise promise);


}
