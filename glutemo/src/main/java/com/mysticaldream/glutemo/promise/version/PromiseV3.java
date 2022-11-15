package com.mysticaldream.glutemo.promise.version;

import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author MysticalDream
 */
public class PromiseV3<T> {

    private AbstractTaskLoopExecutor executor;

    private volatile State state;

    private Lock stateLock;

    private volatile Throwable throwable;

    private T value;

    private Deque<Runnable> resolvedCallbacks;

    private Deque<Runnable> rejectedCallbacks;


    private enum State {
        fulfilled, rejected, pending
    }


    public PromiseV3(AbstractTaskLoopExecutor executor) {
        this.executor = executor;
        this.state = State.pending;
        this.resolvedCallbacks = new ConcurrentLinkedDeque<>();
        this.rejectedCallbacks = new ConcurrentLinkedDeque<>();
        this.stateLock = new ReentrantLock();
    }


    public void resolve(T t) {
        if (this.state == State.pending) {
            stateLock.lock();
            try {
                if (this.state == State.pending) {
                    this.state = State.fulfilled;
                    this.value = t;

                    while (!this.resolvedCallbacks.isEmpty()) {
                        this.resolvedCallbacks.pop().run();
                    }
                }
            } finally {
                stateLock.unlock();
            }
        }
    }

    public void reject(Throwable throwable) {
        if (this.state == State.pending) {
            stateLock.lock();
            try {
                if (this.state == State.pending) {
                    this.state = State.rejected;
                    this.throwable = throwable;
                    while (!this.rejectedCallbacks.isEmpty()) {
                        this.rejectedCallbacks.pop().run();
                    }
                }
            } finally {
                stateLock.unlock();
            }
        }
    }

    public <R> PromiseV3<R> then(Function<? super T, ? extends R> fn) {

        if (fn == null) {
            throw new NullPointerException();
        }


        PromiseV3<R> promise = new PromiseV3<>(executor);


        Runnable success = () -> {
            try {
                promise.resolve(fn.apply(value));
            } catch (Exception e) {
                promise.reject(e);
            }
        };

        if (this.state == State.fulfilled) {
            success.run();
        } else if (this.state == State.rejected) {
            promise.reject(throwable);
        } else if (this.state == State.pending) {
            resolvedCallbacks.push(success);
            rejectedCallbacks.push(() -> {
                promise.reject(throwable);
            });
        }
        return promise;
    }


    public PromiseV3<T> exceptionCatch(Function<Throwable, ? extends T> fn) {

        if (fn == null) {
            throw new NullPointerException();
        }

        PromiseV3<T> promise = new PromiseV3<>(executor);

        Runnable runnable = () -> {
            try {
                promise.resolve(fn.apply(throwable));
            } catch (Exception e) {
                promise.reject(e);
            }
        };

        if (this.state == State.rejected) {
            runnable.run();
        } else if (this.state == State.pending) {
            rejectedCallbacks.push(runnable);
        }
        return promise;
    }

}
