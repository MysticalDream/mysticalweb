import com.mysticaldream.glutemo.concurrent.SimpleTaskLoopExecutorGroup;
import com.mysticaldream.glutemo.promise.version.Promise;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author MysticalDream
 */
public class FutureTest {

    @Test
    public void test() {

        SimpleTaskLoopExecutorGroup taskLoopExecutorGroup = new SimpleTaskLoopExecutorGroup();

        Promise<String> promise = Promise.newPromise(String.class, taskLoopExecutorGroup.next());


        promise.then(future -> {
            System.out.println(future);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {

            }
            return future;
        }).then(future -> {
            System.out.println(Thread.currentThread().getName());
            int i = 1 / 0;
            System.out.println(future);
            return future;
        }).exceptionCatch(e -> {
            System.out.println(e.getMessage());
            return null;
        });

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            promise.reject(new RuntimeException("123"));
        });

        System.out.println("haha");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
