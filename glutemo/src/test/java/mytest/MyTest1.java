package mytest;

import org.junit.jupiter.api.Test;

/**
 * @author MysticalDream
 */
public class MyTest1 {

    @Test
    public void test() {
        SimpleThreadPool simpleThreadPool = new SimpleThreadPool();
        simpleThreadPool.doStartThread();

        simpleThreadPool.execute(() -> {
            System.out.println("==============123===============");
            simpleThreadPool.execute(() -> {
                System.out.println(" 这是一个任务111111111");
            });

            simpleThreadPool.execute(() -> {
                System.out.println("这个也是一个任务222222222");
            });
        });

        try {
            simpleThreadPool.thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void test2() {
        byte[] a = new byte[10];
        Byte[] b = new Byte[10];
        System.out.println(a.getClass().getSimpleName());
        System.out.println(b.getClass().getSimpleName());
    }

}
