package com.mysticaldream.glutemo.promise.version;

import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 承诺
 *
 * @author MysticalDream
 */
public class Promise<T> {


    private AbstractTaskLoopExecutor executor;

    private final CompletableFuture<T> future;

    protected Promise(AbstractTaskLoopExecutor executor) {
        this(executor, new CompletableFuture<>());
    }

    protected Promise(AbstractTaskLoopExecutor executor, CompletableFuture<T> completableFuture) {
        this.executor = executor;
        this.future = completableFuture;
    }

    public void resolve(T t) {
        if (executor.inLoop()) {
            future.complete(t);
        } else {
            executor.execute(() -> {
                future.complete(t);
            });
        }

    }

    public void reject(Throwable throwable) {
        if (executor.inLoop()) {
            future.completeExceptionally(throwable);
        } else {
            executor.execute(() -> {
                future.completeExceptionally(throwable);
            });
        }
    }

    public <U> Promise<U> then(Function<? super T, ? extends U> fn) {
        return new Promise<>(executor, future.thenApply(fn));
    }

    public Promise<T> exceptionCatch(Function<Throwable, ? extends T> function) {
        return new Promise<>(executor, future.exceptionally(function));
    }

    public static <C> Promise<C> newPromise(Class<C> cClass, AbstractTaskLoopExecutor executor) {
        return new Promise<>(executor);
    }
}
