package com.tea.store.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.tea.store.bean.MyRunnable;
import com.tea.store.bean.UDPMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPClient extends MyRunnable {
    private final Gson gson = new Gson();
    private static final int SERVICE_PORT = 8659;
    private static final int MAX_BYTES = 60 * 1000;
    private DatagramSocket socket;
    private Callback callback;

    public UDPClient(Context context){
        try {
            socket = new DatagramSocket();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean send(byte[] bytes) {
        boolean success = true;
        try {
            InetAddress address = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, SERVICE_PORT);
            socket.send(packet);
        }catch (Exception e){
            success = false;
            e.printStackTrace();
        }finally {
            return success;
        }
    }

    public void destory(){
        interrupt();
        if (socket != null) socket.close();
    }

    @Override
    public void run() {
        byte[] receiveBytes = new byte[MAX_BYTES];
        DatagramPacket packet = new DatagramPacket(receiveBytes,receiveBytes.length);
        while (!isInterrupt()){
            try {
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                if (callback != null) callback.onCallback(gson.fromJson(data, UDPMessage.class));
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onCallback(UDPMessage message);
    }
}
