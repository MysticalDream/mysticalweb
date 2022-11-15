package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author MysticalDream
 */
public class UDPServer {


    public static void main(String[] args) throws Exception {

        DatagramSocket datagramSocket = new DatagramSocket(8080);

        String address2 = "localhost";

        byte[] buf = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

        System.out.println("****服务器端1已经启动，等待客户端发送数据");

        while (true) {

            datagramSocket.receive(datagramPacket);

            String info = new String(buf, 0, datagramPacket.getLength());

            System.out.println("我是服务器，客户端说：" + info);

            InetAddress address = datagramPacket.getAddress();

            int port = datagramPacket.getPort();

            byte[] data1 = ("ip:" + address + ",port:" + port).getBytes();

            DatagramPacket packet1 = new DatagramPacket(data1, data1.length, InetAddress.getByName(address2), 80);

            datagramSocket.send(packet1);

        }


    }

}
