package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;
import com.mysticaldream.glutemo.promise.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author MysticalDream
 */
@Slf4j
public abstract class AbstractChannelHandlerContext implements ChannelHandlerContext {
    private final int NOT_SET = -1;

    private final int IN = 1 << 0;

    private final int OUT = 1 << 1;

    private final int BOTH = IN | OUT;


    volatile AbstractChannelHandlerContext prior;

    volatile AbstractChannelHandlerContext next;

    private AtomicInteger mask = new AtomicInteger(NOT_SET);

    private AbstractTaskLoopExecutor taskLoopExecutor;

    private String name;

    private ChannelPipeline pipeline;

    public AbstractChannelHandlerContext(ChannelPipeline pipeline, AbstractTaskLoopExecutor taskLoopExecutor, String name, Class<?> handlerClass) {
        this.taskLoopExecutor = taskLoopExecutor;
        this.name = name;
        this.mask.set(getMask(handlerClass));
        this.pipeline = pipeline;
    }


    private int getMask(Class<?> clazz) {
        int mask;
        if (ChannelInHandler.class.isAssignableFrom(clazz)) {
            mask = IN;
            if (ChannelOutHandler.class.isAssignableFrom(clazz)) {
                mask = BOTH;
            }
        } else if (ChannelOutHandler.class.isAssignableFrom(clazz)) {
            mask = OUT;
        } else {
            throw new IllegalArgumentException("未知的类型 " + clazz.getSimpleName());
        }
        return mask;
    }

    @Override
    public AbstractTaskLoopExecutor getTaskLoopExecutor() {
        if (taskLoopExecutor == null) {
            return (AbstractTaskLoopExecutor) channel().reactor();
        }
        return taskLoopExecutor;
    }


    private AbstractChannelHandlerContext findNextInHandler(int mask) {
        AbstractChannelHandlerContext handlerContext = next;
        while (handlerContext != null && ((handlerContext.mask.get() & mask) == 0)) {
            handlerContext = handlerContext.next;
        }
        return handlerContext;
    }

    private AbstractChannelHandlerContext findNextOutHandler(int mask) {
        AbstractChannelHandlerContext handlerContext = prior;
        while (handlerContext != null && ((handlerContext.mask.get() & mask) == 0)) {
            handlerContext = handlerContext.prior;
        }
        return handlerContext;
    }

    @Override
    public ChannelHandlerContext propagateRegisteredEvent() {
        invokeChannelRegistered(findNextInHandler(IN));
        return this;
    }

    static void invokeChannelRegistered(AbstractChannelHandlerContext nextInHandler) {
        if (nextInHandler != null) {
            AbstractTaskLoopExecutor loopExecutor = nextInHandler.getTaskLoopExecutor();
            if (loopExecutor.inLoop()) {
                nextInHandler.invokeChannelRegistered0();
            } else {
                loopExecutor.execute(() -> {
                    nextInHandler.invokeChannelRegistered0();
                });
            }
        }
    }


    void invokeChannelRegistered0() {
        try {
            ((ChannelInHandler) getHandler()).channelRegistered(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ChannelHandlerContext propagateReadEvent(Object msg) {
        invokeChannelRead(findNextInHandler(IN), msg);
        return this;
    }

    static void invokeChannelRead(AbstractChannelHandlerContext nextHandlerContext, Object msg) {
        if (nextHandlerContext != null) {
            AbstractTaskLoopExecutor loopExecutor = nextHandlerContext.getTaskLoopExecutor();
            if (loopExecutor.inLoop()) {
                nextHandlerContext.invokeChannelRead0(msg);
            } else {
                loopExecutor.execute(() -> nextHandlerContext.invokeChannelRead0(msg));
            }
        }
    }


    void invokeChannelRead0(Object msg) {
        try {
            ((ChannelInHandler) getHandler()).channelRead(this, msg);
        } catch (Exception e) {
            invokeExceptionCaught0(e);
        }
    }


    @Override
    public ChannelPromise write(Object msg) {
        return write(msg, false);
    }

    @Override
    public ChannelPromise write(Object msg, ChannelPromise promise) {
        return write(msg, false, promise);
    }

    @Override
    public ChannelPromise writeAndFlush(Object msg) {
        return write(msg, true);
    }

    @Override
    public ChannelPromise writeAndFlush(Object msg, ChannelPromise promise) {
        return write(msg, true, promise);
    }

    @Override
    public ChannelPromise write(Object msg, boolean flush) {
        return write(msg, flush, ChannelPromise.newChannelPromise(channel(), getTaskLoopExecutor()));
    }

    private ChannelPromise write(Object msg, boolean flush, ChannelPromise channelPromise) {
        invokeWrite(msg, flush, channelPromise, findNextOutHandler(OUT));
        return channelPromise;
    }

    static void invokeWrite(Object msg, boolean flush, ChannelPromise channelPromise, AbstractChannelHandlerContext nextOutHandler) {

        if (nextOutHandler == null) {
            return;
        }

        AbstractTaskLoopExecutor taskLoopExecutor1 = nextOutHandler.getTaskLoopExecutor();

        if (taskLoopExecutor1.inLoop()) {
            if (flush) {
                nextOutHandler.invokeWriteAndFlush(msg, channelPromise);
            } else {
                nextOutHandler.invokeWrite0(msg, channelPromise);
            }
        } else {
            //TODO 不用每次都唤醒，只有flush的时候才需要,实现lazyExecute
            taskLoopExecutor1.execute(() -> {
                if (flush) {
                    nextOutHandler.invokeWriteAndFlush(msg, channelPromise);
                } else {
                    nextOutHandler.invokeWrite0(msg, channelPromise);
                }
            });
        }
    }

    void invokeWriteAndFlush(Object msg, ChannelPromise channelPromise) {
        invokeWrite0(msg, channelPromise);
        invokeFlush0();
    }

    void invokeWrite0(Object msg, ChannelPromise channelPromise) {
        try {
            ((ChannelOutHandler) getHandler()).write(this, msg, channelPromise);
        } catch (Exception e) {
            channelPromise.reject(e);
        }
    }

    @Override
    public AbstractChannelHandlerContext flush() {
        invokeFlush(findNextInHandler(OUT));
        return this;
    }

    static void invokeFlush(AbstractChannelHandlerContext nextHandlerContext) {
        if (nextHandlerContext != null) {
            AbstractTaskLoopExecutor loopExecutor = nextHandlerContext.getTaskLoopExecutor();
            if (loopExecutor.inLoop()) {
                nextHandlerContext.invokeFlush0();
            } else {
                loopExecutor.execute(() -> nextHandlerContext.invokeFlush0());
            }
        }
    }

    void invokeFlush0() {
        try {
            ((ChannelOutHandler) getHandler()).flush(this);
        } catch (Exception e) {
            invokeExceptionCaught0(e);
        }
    }


    @Override
    public ChannelHandlerContext propagateExceptionCaughtEvent(Throwable throwable) {
        invokeExceptionCaught(findNextInHandler(IN), throwable);
        return this;
    }

    static void invokeExceptionCaught(AbstractChannelHandlerContext context, Throwable throwable) {
        if (context == null) {
            return;
        }
        AbstractTaskLoopExecutor taskLoopExecutor1 = context.getTaskLoopExecutor();
        if (taskLoopExecutor1.inLoop()) {
            context.invokeExceptionCaught0(throwable);
        } else {
            taskLoopExecutor1.execute(() -> {
                context.invokeExceptionCaught0(throwable);
            });
        }
    }

    void invokeExceptionCaught0(Throwable throwable) {
        try {
            ((ChannelInHandler) getHandler()).exceptionCaught(this, throwable);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("处理异常时出现异常", e);
            }
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public AbstractNioChannel channel() {
        return pipeline.channel();
    }
}
