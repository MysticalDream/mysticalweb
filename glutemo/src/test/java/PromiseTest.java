import com.mysticaldream.glutemo.promise.Promise;
import com.mysticaldream.glutemo.promise.version.PromiseV2;
import com.mysticaldream.glutemo.promise.version.PromiseV3;
import org.junit.jupiter.api.Test;

/**
 * @author MysticalDream
 */
public class PromiseTest {


    @Test
    public void test1() {

        PromiseV2<String> promiseV2 = new PromiseV2<>(null);


//        PromiseV2<Boolean> promiseV21 =
        promiseV2.then((v) -> {
            System.out.println(v);
            return true;
        }).then((b) -> {
            System.out.println(b);
            return "233";
        }).exceptionCatch((ex) -> {
            System.out.println("ex1:" + ex);
            return null;
        });
        ;
//        PromiseV2<String> promiseV22 = promiseV21.then((b) -> {
//            System.out.println(b);
//            return "233";
//        });
//        PromiseV2<String> promiseV23 = promiseV22.exceptionCatch((ex) -> {
//            System.out.println("ex1:" + ex);
//            return null;
//        });

        promiseV2.exceptionCatch((ex) -> {
            System.out.println("ex2:" + ex);
            return null;
        });

        promiseV2.reject(new RuntimeException("运行时异常"));

        System.out.println("123");

    }


    @Test
    public void test2() {

        PromiseV3<String> promiseV3 = new PromiseV3<>(null);


        promiseV3.then((v) -> {
            System.out.println("1:" + v);
            return true;
        });

        promiseV3.then((b) -> {
            System.out.println("2:" + b);
            return "233";
        });

        promiseV3.exceptionCatch((ex) -> {
            System.out.println("ex1:" + ex);
            return null;
        });

        promiseV3.exceptionCatch((ex) -> {
            System.out.println("ex2:" + ex);
            return null;
        });

//        promiseV3.reject(new RuntimeException("运行时异常"));
        promiseV3.resolve("123");

    }

    @Test
    public void test3() {

        Promise<String> promise = new Promise<>(null);

        promise.then((v) -> {
            System.out.println("1:" + v);
            return true;
        });

        promise.then((b) -> {
            System.out.println("2:" + b);
            return "233";
        });

        promise.exceptionCatch((ex) -> {
            System.out.println("ex1:" + ex);
            return null;
        });

        promise.exceptionCatch((ex) -> {
            System.out.println("ex2:" + ex);
            return null;
        });

        promise.reject(new RuntimeException("运行时异常"));
//        promise.resolve("123");
        System.out.println("hahaha");
    }


    @Test
    public void test4() {

        Promise<String> promise = new Promise<>(null);

        promise.then((v) -> {
            System.out.println("1:" + v);
            return true;
        }).then((b) -> {
            System.out.println("2:" + b);
            return "233";
        }).exceptionCatch((ex) -> {
            System.out.println("ex1:" + ex);
            return null;
        });

        promise.exceptionCatch((ex) -> {
            System.out.println("ex2:" + ex);
            return null;
        });

        promise.reject(new RuntimeException("运行时异常"));
//        promiseV4.resolve("123");
        System.out.println("hahaha");
    }
}
