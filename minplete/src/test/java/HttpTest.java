import com.mysticaldream.minplte.http.HttpParser2;
import org.junit.jupiter.api.Test;

/**
 * @author MysticalDream
 */
public class HttpTest {

    @Test
    public void test1() {
        HttpParser2 httpParser2 = new HttpParser2();
        byte[] bytes = new String("").getBytes();
        httpParser2.parse(bytes);
    }

}
