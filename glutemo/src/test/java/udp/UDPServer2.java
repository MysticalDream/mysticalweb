package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author MysticalDream
 */
public class UDPServer2 {

    public static void main(String[] args) throws Exception {

        DatagramSocket datagramSocket = new DatagramSocket(80);


        System.out.println("****服务器端2已经启动[:80]****");

        while (true) {

            System.out.println("等待接收数据");

            byte[] buf = new byte[1024];

            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

            datagramSocket.receive(datagramPacket);

            String info = new String(buf, 0, datagramPacket.getLength());

            InetAddress address = datagramPacket.getAddress();

            int port1 = datagramPacket.getPort();

            System.out.println("收到来自[" + address + ":" + port1 + "]的数据 ----->" + info);

            String[] split = info.split(",");

            String ip = split[0].substring(split[0].indexOf("ip:/") + 4);

            String port = split[1].substring(split[1].indexOf("port:") + 5);

            System.out.println("即将向 " + ip + ":" + port + " 发送数据");

            byte[] data1 = ("ip:" + ip + ",port:" + port).getBytes();

            DatagramPacket packet1 = new DatagramPacket(data1, data1.length, InetAddress.getByName(ip), Integer.parseInt(port));

            datagramSocket.send(packet1);

            System.out.println("发送成功");
        }

    }

}
