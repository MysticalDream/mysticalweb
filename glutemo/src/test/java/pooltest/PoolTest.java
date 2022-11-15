package pooltest;

import com.mysticaldream.glutemo.concurrent.SimpleTaskLoopExecutorGroup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author MysticalDream
 */
public class PoolTest {


    @SneakyThrows
    @Test
    public void test() {
        SimpleTaskLoopExecutorGroup taskLoopExecutorGroup = new SimpleTaskLoopExecutorGroup();

        taskLoopExecutorGroup.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("hello world!");
            System.out.println(Thread.currentThread().getName());

        });

        taskLoopExecutorGroup.execute(() -> {
            System.out.println("hello world111111!");
            System.out.println(Thread.currentThread().getName());
        });

        Thread.sleep(10000);
    }

}
