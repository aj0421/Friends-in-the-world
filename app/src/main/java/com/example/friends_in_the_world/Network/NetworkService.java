package com.example.friends_in_the_world.Network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.friends_in_the_world.Controllers.MainController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkService extends Service {
    public static final String TAG = "NetworkService";
    public static String SERVER_IP = "SERVER_IP";
    public static String SERVER_PORT = "SERVER_PORT";

    private InetAddress address;
    private int port;
    //
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private  RunOnThread thread;
    private boolean connected = false;
    //
    private Receive receive;

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            address = InetAddress.getByName(intent.getStringExtra(SERVER_IP));
            port = Integer.parseInt(intent.getStringExtra(SERVER_PORT));
            thread = new RunOnThread();
            thread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalService();
    }

    public class LocalService extends Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void connect() {
        thread.start();
        thread.execute(new Connect());
    }

    public void sendMessage(String message){
        if (!connected) {
            thread.execute(new Connect());
        }
        thread.execute(new Send(message));
    }

    private void closeConnection() {

        try {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
            if (socket != null)
                socket.close();
            if (receive != null) {
                receive.interrupt();
                receive = null;
            }
            Log.d(TAG, "Disconnected!");
        } catch (IOException e) {
            Log.e(TAG, "Error disconnecting: ", e);
        } finally {
            connected = false;
        }
    }

    private class Connect implements Runnable {
        public void run() {
            if (receive != null && socket != null && !socket.isClosed() && socket.isConnected() &&
                    !socket.isInputShutdown() && !socket.isOutputShutdown()) {
                Log.d(TAG, "Already connected!");
                return;
            }
            // Make sure we're disconnected properly
            closeConnection();
            Log.d(TAG, "Connecting to the server...");
            try {
                socket = new Socket(address, port);
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                output.flush();
            } catch (IOException e) {
                Log.e(TAG, "Error connecting: ", e);
            }
            // Start the receive thread
            receive = new Receive();
            receive.start();
            connected = true;
        }
    }

    private class Receive extends Thread {
        public void run() {
            try {
                while (receive != null) {
                    String result = input.readUTF();
                    ((MainController) getApplication()).process(result);
                    Log.d(TAG, "Received message: " + result);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error receiving message: ", e);
                receive = null;
                connect(); // Reconnect
            }
        }
    }

    private class Send implements Runnable {
        private String message;

        Send(String message) {
            this.message = message;
        }

        public void run() {
            try {
                output.writeUTF(message);
                output.flush();
                Log.d(TAG, "Sent message:" + message);
            } catch (IOException e) {
                Log.e(TAG, "Error sending message: ", e);
            }
        }
    }
}