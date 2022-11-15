package file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author MysticalDream
 */
public class MyTest {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("C:/Users/18177/.android/avd/Pixel_2_API_29.avd/cache.img");

        Map<String, Object> stringObjectMap = Files.readAttributes(path, "*");

        stringObjectMap.forEach((String k, Object v) -> {
            System.out.println("key:" + k);
            System.out.println("value:" + v);
        });


    }
}
