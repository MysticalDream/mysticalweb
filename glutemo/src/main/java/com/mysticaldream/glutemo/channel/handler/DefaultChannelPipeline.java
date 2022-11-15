package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.concurrent.SimpleTaskLoopExecutorGroup;
import com.mysticaldream.glutemo.promise.ChannelPromise;
import com.mysticaldream.glutemo.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MysticalDream
 */
@Slf4j
public class DefaultChannelPipeline implements ChannelPipeline {

    private AbstractChannelHandlerContext head;

    private AbstractChannelHandlerContext tail;
    private final AbstractNioChannel channel;


    public DefaultChannelPipeline(AbstractNioChannel channel) {
        this.channel = channel;
        this.head = new HeadContext(this);
        this.tail = new TailContext(this);

        this.head.next = tail;
        this.tail.prior = head;
    }


    class HeadContext extends AbstractChannelHandlerContext implements ChannelInHandler, ChannelOutHandler {

        private AbstractNioChannel channel;

        public HeadContext(ChannelPipeline pipeline) {
            super(pipeline, null, "head", HeadContext.class);
            this.channel = pipeline.channel();
        }

        @Override
        public ChannelHandler getHandler() {
            return this;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext context) throws Exception {
            context.propagateRegisteredEvent();
        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
            context.propagateReadEvent(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
            context.propagateExceptionCaughtEvent(throwable);
        }

        @Override
        public void write(ChannelHandlerContext context, Object msg, ChannelPromise channelPromise) throws Exception {
            channel.write(msg, channelPromise);
        }

        @Override
        public void flush(ChannelHandlerContext context) throws Exception {
            channel.notifyFlush();
        }
    }

    class TailContext extends AbstractChannelHandlerContext implements ChannelInHandler {

        public TailContext(ChannelPipeline pipeline) {
            super(pipeline, null, "tail", TailContext.class);
        }

        @Override
        public ChannelHandler getHandler() {
            return this;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext context) throws Exception {

        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
            if (log.isDebugEnabled()) {
                log.debug("消息没有被处理{}", msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
            log.warn("异常到达尾部handler", throwable);
        }
    }


    @Override
    public ChannelPipeline propagateRegisteredEvent() {
        AbstractChannelHandlerContext.invokeChannelRegistered(head);
        return this;
    }

    @Override
    public ChannelPipeline propagateReadEvent(Object msg) {
        AbstractChannelHandlerContext.invokeChannelRead(head, msg);
        return this;
    }

    @Override
    public ChannelPipeline propagateExceptionCaughtEvent(Throwable throwable) {
        AbstractChannelHandlerContext.invokeExceptionCaught(head, throwable);
        return this;
    }

    @Override
    public ChannelPromise write(Object msg, boolean flush) {
        return tail.write(msg, flush);
    }

    @Override
    public ChannelPipeline flush() {
        tail.flush();
        return this;
    }

    @Override
    public ChannelPromise write(Object msg) {
        return tail.write(msg);
    }

    @Override
    public ChannelPromise write(Object msg, ChannelPromise promise) {
        return tail.write(msg, promise);
    }

    @Override
    public ChannelPromise writeAndFlush(Object msg) {
        return tail.writeAndFlush(msg);
    }

    @Override
    public ChannelPromise writeAndFlush(Object msg, ChannelPromise promise) {
        return tail.writeAndFlush(msg, promise);
    }

    @Override
    public ChannelPipeline addFirst(String name, ChannelHandler channelHandler) {
        AbstractChannelHandlerContext channelHandlerContext = new DefaultChannelHandlerContext(this, null, name, channelHandler);

        addFirst0(channelHandlerContext);

        return this;
    }

    private void addFirst0(AbstractChannelHandlerContext newContext) {
        AbstractChannelHandlerContext nextContext = this.head.next;
        this.head.next = newContext;
        newContext.prior = head;
        newContext.next = nextContext;
        nextContext.prior = newContext;
    }

    @Override
    public ChannelPipeline addFirst(SimpleTaskLoopExecutorGroup executor, ChannelHandler channelHandler) {
        DefaultChannelHandlerContext channelHandlerContext = new DefaultChannelHandlerContext(this, executor.next(), generateName(channelHandler.getClass()), channelHandler);
        addFirst0(channelHandlerContext);
        return this;
    }


    private String generateName(Class<?> clazz) {
        return clazz.getSimpleName() + "#" + UUIDUtils.uuid();
    }


    private void addLast0(AbstractChannelHandlerContext next) {
        AbstractChannelHandlerContext prior = tail.prior;
        tail.prior = next;
        next.next = tail;
        next.prior = prior;
        prior.next = next;
    }

    @Override
    public ChannelPipeline addLast(String name, ChannelHandler channelHandler) {
        DefaultChannelHandlerContext handlerContext = new DefaultChannelHandlerContext(this, null, name, channelHandler);
        addLast0(handlerContext);
        return this;
    }

    @Override
    public ChannelPipeline addLast(SimpleTaskLoopExecutorGroup executor, ChannelHandler channelHandler) {
        DefaultChannelHandlerContext channelHandlerContext = new DefaultChannelHandlerContext(this, executor.next(), generateName(channelHandler.getClass()), channelHandler);
        addLast0(channelHandlerContext);
        return this;
    }

    @Override
    public ChannelPipeline addFirst(ChannelHandler channelHandler) {
        addFirst(generateName(channelHandler.getClass()), channelHandler);
        return this;
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler channelHandler) {
        addLast(generateName(channelHandler.getClass()), channelHandler);
        return this;
    }

    @Override
    public AbstractNioChannel channel() {
        return channel;
    }


    @Override
    public boolean isEmpty() {
        return false;
    }
}
