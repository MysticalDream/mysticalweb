import com.mysticaldream.minplte.http.HttpParser2;
import org.junit.jupiter.api.Test;

/**
 * @author MysticalDream
 */
public class Test01 {

    @Test
    public void test() {
        HttpParser2 httpParser2 = new HttpParser2();
        byte[] bytes = {6, 4, 3, 2, 6, 1, 2, 3, 4, 5, 9};
        int i = httpParser2.indexOf(bytes, new byte[]{1, 2, 3});
        System.out.println(i);
    }

}
