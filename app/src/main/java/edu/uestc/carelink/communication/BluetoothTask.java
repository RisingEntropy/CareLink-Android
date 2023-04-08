package edu.uestc.carelink.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BluetoothTask implements Runnable{
    private static final String TAG = "BluetoothTask";
    private BluetoothSocket socket;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    public interface ReceiveCompleteCallback{
        void receiveComplete(String data);
    }
    public interface TransmitCompleteCallback{
        String transmitComplete(int state);

    }
    public BluetoothTask(BluetoothSocket socket){
        this.socket = socket;
        try {
            this.inputStream = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
    }
    public void execute(){
        new Thread(this,"BluetoothTaskThread").start();
    }
}
