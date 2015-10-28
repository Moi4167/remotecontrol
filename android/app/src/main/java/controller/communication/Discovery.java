package controller.communication;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Discovery {
    public static final int RECEIVING_TIMEOUT = 10000;
    public static final int RECEIVING_TIMEOUT_SERVER = 30000;
    private static final int mPort= 8888;
    private DatagramSocket socket;
    private Context mContext;


    public Discovery(Context c) {
        mContext = c;
    }

    public void stop() {

        try
        {
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getServerIp() {
        try
        {
            DatagramPacket packet = sendBroadcast("Ping");
            return packet.getAddress().getHostAddress();
        }
        catch (InterruptedIOException ie)
        {
            Log.d("ERROR", "No server found");
            try
            {
                socket.close();
            }
            catch (Exception e2) {}
            ie.printStackTrace();
        }
        catch (Exception e)
        {
            Log.d("ERROR", "Verify your Wifi connection");
            try
            {
                socket.close();
            }
            catch (Exception e2) {}
            e.printStackTrace();
        }

        stop();
        return null;
    }

    public void waitingForDiscover() {
        socket = null;
        try
        {
            socket = new DatagramSocket(mPort);
            socket.setSoTimeout(RECEIVING_TIMEOUT_SERVER);
        }
        catch (SocketException se)
        {
            Log.d("ERROR", "Verify your Wifi connection");
            se.printStackTrace();
        }

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        int i = 0;
        while (socket != null && !socket.isClosed())
        {
            try
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                if (sentence.contains("Ping"))
                {
                    sendData = "Pong".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    socket.send(sendPacket);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            i++;
        }
        stop();
    }

    public String getMyAdresseIP() {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                        return inetAddress.getHostAddress();
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private InetAddress getBroadcastAddress() throws Exception {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    private DatagramPacket sendBroadcast(String data) throws Exception {
        socket = new DatagramSocket(mPort);
        socket.setBroadcast(true);
        InetAddress broadcastAdress = getBroadcastAddress();
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), broadcastAdress, mPort);
        socket.send(packet);

        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);
        socket.setSoTimeout(RECEIVING_TIMEOUT);

        String myAdress =getMyAdresseIP();

        socket.receive(packet);
        while (packet.getAddress().getHostAddress().contains(myAdress))
            socket.receive(packet);

        stop();
        return packet;
    }
}
