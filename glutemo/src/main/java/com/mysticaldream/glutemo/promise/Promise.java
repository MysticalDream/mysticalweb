package com.mysticaldream.glutemo.promise;

import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;
import com.mysticaldream.glutemo.concurrent.SimpleTaskLoopExecutor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * 回调处理
 *
 * @author MysticalDream
 */
public class Promise<T> {

    private AbstractTaskLoopExecutor executor;

    private volatile State state;

    private Throwable throwable;

    private T value;

    private Deque<Runnable> resolvedCallbacks;

    private Deque<Runnable> rejectedCallbacks;


    private enum State {
        pending, fulfilled, rejected
    }

    public Promise() {
        this(null);
    }

    public Promise(AbstractTaskLoopExecutor executor) {
        this.executor = executor == null ? new SimpleTaskLoopExecutor(Executors.newSingleThreadExecutor()) : executor;
        this.state = State.pending;
        this.resolvedCallbacks = new LinkedList<>();
        this.rejectedCallbacks = new LinkedList<>();
    }


    public void resolve(T t) {

        Runnable runnable = () -> {
            if (this.state == State.pending) {
                this.state = State.fulfilled;
                this.value = t;
                while (!this.resolvedCallbacks.isEmpty()) {
                    this.resolvedCallbacks.pop().run();
                }
            }
        };

        if (executor.inLoop()) {
            runnable.run();
        } else {
            executor.execute(runnable);
        }

    }

    public void reject(Throwable throwable) {

        Runnable runnable = () -> {
            if (this.state == State.pending) {
                this.state = State.rejected;
                this.throwable = throwable;
                while (!this.rejectedCallbacks.isEmpty()) {
                    this.rejectedCallbacks.pop().run();
                }
            }
        };

        if (executor.inLoop()) {
            runnable.run();
        } else {
            executor.execute(runnable);
        }

    }

    public <R> Promise<R> then(Function<? super T, ? extends R> fn) {

        if (fn == null) {
            throw new NullPointerException();
        }


        Promise<R> promise = new Promise<>(executor);


        Runnable runnable = () -> {

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

        };


        if (executor.inLoop()) {
            runnable.run();
        } else {
            executor.execute(runnable);
        }


        return promise;
    }


    public Promise<T> exceptionCatch(Function<Throwable, ? extends T> fn) {

        if (fn == null) {
            throw new NullPointerException();
        }

        Promise<T> promise = new Promise<>(executor);


        Runnable runnable = () -> {

            Runnable reject = () -> {
                try {
                    promise.resolve(fn.apply(throwable));
                } catch (Exception e) {
                    promise.reject(e);
                }
            };

            if (this.state == State.rejected) {
                reject.run();
            } else if (this.state == State.pending) {
                rejectedCallbacks.push(reject);
            }
        };

        if (executor.inLoop()) {
            runnable.run();
        } else {
            executor.execute(runnable);
        }

        return promise;
    }

}
