import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author MysticalDream
 */
public class BlockingTest {

    @Test
    public void test() throws InterruptedException {
        LinkedBlockingDeque<String> deque = new LinkedBlockingDeque<>(1);

        System.out.println(deque.offer("1"));
        System.out.println(deque.offer("2"));
        System.out.println(deque);
    }

}
