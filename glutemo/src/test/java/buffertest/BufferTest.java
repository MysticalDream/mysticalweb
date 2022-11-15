package buffertest;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author MysticalDream
 */
public class BufferTest {
    public static void main(String[] args) throws Exception {
        String file = "D:\\桌面\\50GB.txt";
//        readTest1(file);
        readTest2(file);
    }


    public static void readTest1(String file) throws Exception {
        long start = System.currentTimeMillis();

        RandomAccessFile fis = new RandomAccessFile(new File(file), "r");

        FileChannel channel = fis.getChannel();

        long size = channel.size();

        int cntSize = 104857600;

        int cnt = (int) (size / cntSize);
        int mode = (int) (size & (cntSize - 1));
        int i = 0;
        for (; i < cnt; i += cnt) {
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, i, cntSize);
            byte[] buf = new byte[cntSize];
            mappedByteBuffer.get(buf);
        }

        if (mode > 0) {
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, i, size);
            byte[] buf = new byte[(int) (size - i)];
            mappedByteBuffer.get(buf);
        }

        // 关闭通道和文件流
        channel.close();
        fis.close();

        long end = System.currentTimeMillis();
        System.out.println(String.format("文件大小：%s 字节", size));
        System.out.println(String.format("耗时：%s毫秒", end - start));
    }

    public static void readTest2(String file) throws Exception {
        long start = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream(file);

        // 1.从FileInputStream对象获取文件通道FileChannel
        FileChannel channel = fis.getChannel();
        long size = channel.size();

        // 2.从通道读取文件内容
        byte[] bytes = new byte[104857600];

        ByteBuffer byteBuffer = ByteBuffer.allocate(104857600);

        // channel.read(ByteBuffer) 方法就类似于 inputstream.read(byte)
        // 每次read都将读取 allocate 个字节到ByteBuffer
        int len;
        while ((len = channel.read(byteBuffer)) != -1) {
            // 注意先调用flip方法反转Buffer,再从Buffer读取数据
            byteBuffer.flip();

            // 有几种方式可以操作ByteBuffer
            // 1.可以将当前Buffer包含的字节数组全部读取出来
            //bytes = byteBuffer.array();
            // System.out.print(new String(bytes));

            // 2.类似与InputStrean的read(byte[],offset,len)方法读取
            byteBuffer.get(bytes, 0, len);
            // System.out.print(new String(bytes, 0 ,len));

            // 3.也可以遍历Buffer读取每个字节数据
            // 一个字节一个字节打印在控制台,但这种更慢且耗时
            // while(byteBuffer.hasRemaining()) {
            // System.out.print((char)byteBuffer.get());
            // }

            // 最后注意调用clear方法,将Buffer的位置回归到0
            byteBuffer.clear();

        }

        // 关闭通道和文件流
        channel.close();
        fis.close();

        long end = System.currentTimeMillis();
        System.out.println(String.format("文件大小：%s 字节", size));
        System.out.println(String.format("耗时：%s毫秒", end - start));
    }
}
