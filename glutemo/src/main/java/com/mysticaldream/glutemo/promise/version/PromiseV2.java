package com.mysticaldream.glutemo.promise.version;

import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author MysticalDream
 */
public class PromiseV2<T> {

    private AbstractTaskLoopExecutor executor;

    private volatile State state;

    private Lock stateLock;

    private volatile Throwable throwable;

    private T value;

    private List<Runnable> resolvedCallbacks;

    private List<Runnable> rejectedCallbacks;


    private enum State {
        fulfilled, rejected, pending
    }


    public PromiseV2(AbstractTaskLoopExecutor executor) {
        this.executor = executor;
        this.state = State.pending;
        this.resolvedCallbacks = new LinkedList<>();
        this.rejectedCallbacks = new LinkedList<>();
        this.stateLock = new ReentrantLock();
    }


    public void resolve(T t) {
        if (this.state == State.pending) {
            stateLock.lock();
            try {
                if (this.state == State.pending) {
                    this.state = State.fulfilled;
                    this.value = t;
                    this.resolvedCallbacks.forEach(Runnable::run);
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
                    this.rejectedCallbacks.forEach(Runnable::run);
                }
            } finally {
                stateLock.unlock();
            }
        }
    }

    public <R> PromiseV2<R> then(Function<? super T, ? extends R> fn) {

        if (fn == null) {
            throw new NullPointerException();
        }


        PromiseV2<R> promise = new PromiseV2<>(executor);


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
            resolvedCallbacks.add(success);
            rejectedCallbacks.add(() -> {
                promise.reject(throwable);
            });
        }
        return promise;
    }


    public PromiseV2<T> exceptionCatch(Function<Throwable, ? extends T> fn) {

        if (fn == null) {
            throw new NullPointerException();
        }

        PromiseV2<T> promise = new PromiseV2<>(executor);

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
            rejectedCallbacks.add(runnable);
        }
        return promise;
    }

}
