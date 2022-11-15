import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author MysticalDream
 */
public class CompletableFutureTest {

    @Test
    public void test() throws Exception {

        System.out.println(Thread.currentThread().getName());

        ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {

            AtomicInteger atomicInteger = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("myPool-1-thread-" + atomicInteger.getAndIncrement());
                return thread;
            }
        });

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {

            System.out.println(Thread.currentThread().getName());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("你好");
            return "success";
        }, executorService);

        System.out.println("v:" + future.get());

    }

    @Test
    public void test2() {

        System.out.println(Thread.currentThread());

        CompletableFuture<Void> future = new CompletableFuture<>();

        if (future.isDone()) {
            System.out.println("done");
        }

//        future.complete(null);

        future.completeExceptionally(new RuntimeException("123"));

        future.thenAccept((e) -> {
            if (future.isDone()) {
                System.out.println("inner");
            }
            System.out.println(Thread.currentThread());
            System.out.println(e);
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                System.out.println(throwable.getMessage());
                return null;
            }
        });


    }


    @Test
    public void test4() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        completableFuture.thenApply((r) -> {
            System.out.println("1:" + r);
            return "123";
        }).thenApply((r) -> {
            System.out.println("2:" + r);
            return "233";
        }).exceptionally((ex) -> {
            System.out.println("ex1:" + ex);
            return "455";
        });

        completableFuture.exceptionally((ex) -> {
            System.out.println("ex2:" + ex);
            return "433";
        });

        completableFuture.completeExceptionally(new RuntimeException("运行时错误"));
        System.out.println("123");

    }

    @Test
    public void test5() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        CompletableFuture<String> a = completableFuture.thenApply((r) -> {
            System.out.println("1:" + r);
            return "123";
        });
        System.out.println("-------");
        CompletableFuture<String> b = completableFuture.thenApply((r) -> {
            System.out.println("2:" + r);
            return "233";
        });

        CompletableFuture<String> c = completableFuture.thenApply((r) -> {
            System.out.println("3:" + r);
            return "666";
        });

        completableFuture.exceptionally((ex) -> {
            System.out.println("ex1:" + ex);
            return "455";
        });

        completableFuture.exceptionally((ex) -> {
            System.out.println("ex2:" + ex);
            return "433";
        });

//        completableFuture.completeExceptionally(new RuntimeException("运行时错误"));
        System.out.println("-------");
        completableFuture.complete("1111");
        System.out.println("-------");
    }

}
