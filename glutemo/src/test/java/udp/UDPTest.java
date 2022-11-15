package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * @author MysticalDream
 */
public class UDPTest {


    public static void main(String[] args) throws Exception {

//        InetAddress address = InetAddress.getByName("106.55.11.62");
        InetAddress address = InetAddress.getByName("localhost");
        int port = 8080;//定义端口类型

        DatagramSocket socket = new DatagramSocket(); // 3.创建DatagramSocket对象

        int maxRetry = 10;

        socket.setSoTimeout(300);


        while (true) {

            Scanner scanner = new Scanner(System.in);//从键盘接受数据
            String send = scanner.nextLine();//nextLine方式接受字符串
            byte[] data = send.getBytes();//将接收到的数据变成字节数组
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);//2.创建数据报，包含发送的数据信息


            socket.send(packet);// 4.向服务器端发送数据报


            while (true) {
                int retryCount = 0;
                boolean hadData = false;

                byte[] data2 = new byte[1024];//创建字节数组
                DatagramPacket packet2 = new DatagramPacket(data2, data2.length);// 1.创建数据报，用于接收服务器端响应的数据

                while (!hadData && retryCount < maxRetry) {
                    try {
                        socket.receive(packet2);// 2.接收服务器响应的数据
                        hadData = true;
                    } catch (SocketTimeoutException e) {
                        retryCount += 1;
                        System.out.println("Time out," + (maxRetry - retryCount) + " more tries...");
                    }
                }
                if (hadData) {
                    //3.读取数据
                    String reply = new String(data2, 0, packet2.getLength());//创建字符串对象
                    System.out.println("我是客户端，服务器说：" + reply);//输出提示信息
                    System.out.println("我是客户端，我本机验证的:ip:" + socket.getLocalAddress() + ",port:" + socket.getLocalPort());
                } else {
                    break;
                }

            }
        }


    }

}
