package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.promise.ChannelPromise;
import com.mysticaldream.glutemo.utils.ByteArrayList;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.InvalidParameterException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 对{@link SocketChannel}的封装
 *
 * @author MysticalDream
 */
@Slf4j
public class NioSocketChannel extends AbstractNioChannel {

    private final Deque<OutBuffer> outBuffers;


    private final ByteBuffer buffer;

    private int outByteCount;

    /**
     * 超过多少就执行刷新暂存消息的操作
     */
    private int flushThreshold = 1024;

    private AtomicBoolean interestingWrite = new AtomicBoolean(false);

    public NioSocketChannel(SelectableChannel sc) {
        super(sc);
        this.outBuffers = new ConcurrentLinkedDeque<>();
        this.buffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void bind(SocketAddress socketAddress) throws IOException {

    }

    @Override
    public int getInterestedOps() {
        return SelectionKey.OP_READ;
    }


    @Override
    public void read() throws IOException {

        SocketChannel socketChannel = javaChannel();

        int readByte;

        ByteArrayList byteArrayList = ByteArrayList.getInstance();

        //TODO 不要一直读取，无法预料未来要接收多大的数据会导致阻塞在这里
        while ((readByte = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            byteArrayList.addAll(buffer.array(), 0, readByte);
            buffer.clear();
        }

        if (byteArrayList.size() > 0) {
            pipeline().propagateReadEvent(byteArrayList.values());
        }

        byteArrayList.clear();

        if (readByte == -1) {
            close();
//            throw new IOException("通道到达流的结尾");
        }


    }

    @Override
    public void write(Object data, ChannelPromise channelPromise) throws Exception {
        if (data instanceof ByteBuffer) {
            ByteBuffer byteBuffer = (ByteBuffer) data;
            outByteCount += (byteBuffer.limit() - byteBuffer.position());
            writeBuffer(byteBuffer, channelPromise);
        } else if ("byte[]".equals(data.getClass().getSimpleName())) {
            byte[] bytes = (byte[]) data;
            outByteCount += bytes.length;
            writeBuffer(ByteBuffer.wrap(bytes), channelPromise);
        }
    }

    /**
     * 将{@link  ByteBuffer}和{@link ChannelPromise}包装并加到 outBuffers队列中
     *
     * @param buffer
     * @param channelPromise
     * @throws Exception
     */
    void writeBuffer(ByteBuffer buffer, ChannelPromise channelPromise) throws Exception {
        outBuffers.offer(new OutBuffer(channelPromise, buffer));
        if (outByteCount >= flushThreshold) {
            notifyFlush();
        }
    }

    @Override
    public void notifyFlush() {
        SelectionKey selectionKey = getSelectionKey();
        if (interestingWrite.compareAndSet(false, true) && (selectionKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
            interestWrite(selectionKey);
        }
    }

    /**
     * 对给出的{@link SelectionKey}添加 {@code SelectionKey.OP_WRITE}事件
     *
     * @param selectionKey
     */
    private void interestWrite(SelectionKey selectionKey) {
        if (reactor().inReactor()) {
            selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
            interestingWrite.compareAndSet(true, false);
        } else {
            reactor().execute(() -> {
                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
                interestingWrite.compareAndSet(true, false);
            });
        }
    }

    @Override
    public void flush() throws Exception {
        flush0();
    }

    /**
     * 将消息写入通道
     * <p>
     * 清空{@code  outBuffers}队列
     *
     * @throws Exception
     */
    private void flush0() throws Exception {

        OutBuffer outBuffer;

        boolean cancelWrite = true;

        while ((outBuffer = outBuffers.peek()) != null) {

            Object data = outBuffer.data;

            boolean writeCompleted;

            if (data instanceof ByteBuffer) {
                writeCompleted = flushByteBuffer(((ByteBuffer) data), outBuffer.channelPromise);
            } else {
                log.warn("还没支持其他类型 {} ", data.getClass());
                throw new InvalidParameterException("不支持的类型 " + data.getClass());
            }
            if (writeCompleted) {
                outBuffers.poll();
            } else {
                cancelWrite = false;
                break;
            }
        }

        if (cancelWrite) {

            SelectionKey selectionKey = getSelectionKey();

            if ((selectionKey.interestOps() & SelectionKey.OP_WRITE) != 0) {
                disinclineWrite(selectionKey);
            }
        }
    }

    /**
     * 将{@link ByteBuffer}写入通道，并在写入后触发{@link ChannelPromise}
     *
     * @param buffer
     * @param promise
     * @throws Exception
     */
    boolean flushByteBuffer(ByteBuffer buffer, ChannelPromise promise) throws Exception {
        try {
            SocketChannel socketChannel = javaChannel();

            int remaining = buffer.remaining();

            int write = socketChannel.write(buffer);

            outByteCount -= write;

            if (write == remaining) {
                promise.resolve(promise);
                return true;
            }
            return false;
        } catch (IOException e) {
            promise.reject(e);
            throw e;
        }
    }

    /**
     * 取消{@link SelectionKey}对{@code  SelectionKey.OP_WRITE}事件的监听
     *
     * @param selectionKey
     */
    private void disinclineWrite(SelectionKey selectionKey) {

        if (reactor().inReactor()) {
            selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
        } else {
            reactor().execute(() -> {
                selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
            });
        }
    }

    @Override
    public boolean isActive() {
        SocketChannel socketChannel = javaChannel();
        return socketChannel.isOpen() && socketChannel.isConnected();
    }


    @Override
    public void close() throws IOException {
        javaChannel().close();
    }

    @Override
    public SocketChannel javaChannel() {
        return (SocketChannel) super.javaChannel();
    }

    /**
     * 包装输出数据{@link Object data}和对应的{@link  ChannelPromise channelPromise}
     */
    class OutBuffer {

        ChannelPromise channelPromise;

        Object data;

        public OutBuffer(ChannelPromise channelPromise, Object data) {
            this.channelPromise = channelPromise;
            this.data = data;
        }
    }

}
