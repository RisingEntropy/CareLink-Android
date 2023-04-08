package edu.uestc.carelink.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

import edu.uestc.carelink.communication.BluetoothTask;
import edu.uestc.carelink.communication.protocol.content.Content;
import edu.uestc.carelink.communication.protocol.header.Header;


//ERROR: headr应该是可以从content里面构造出来的，无需单独列出，后面重构的时候再改吧，现在没时间了
public class BluetoothTransmitTask extends BluetoothTask{
    public static final int STATE_SEND_SUCCESS = 1;
    public static final int STATE_SEND_FAIL = 2;
    private static final String TAG = "BluetoothTransmitTask";
    private OutputStream outputStream;
    private Header header;
    private Content content;
    private TransmitCompleteCallback callback;
    public BluetoothTransmitTask(BluetoothSocket socket, TransmitCompleteCallback callback, Header header, Content content) {
        super(socket);
        this.header = header;
        this.content = content;
        this.callback = callback;
        try {
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
    }

    @Override
    public void run() {
        try {
            this.outputStream.write((header.convertToString()+content.convertToString()).getBytes());
            this.callback.transmitComplete(STATE_SEND_SUCCESS);
        } catch (IOException e) {
            this.callback.transmitComplete(STATE_SEND_FAIL);
        }
    }
}
