package edu.uestc.carelink.communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.lang.reflect.ParameterizedType;

import edu.uestc.carelink.communication.protocol.content.Content;
import edu.uestc.carelink.communication.protocol.header.Header;

public class BluetoothReceiveTask<HeaderType extends Header, ContentType extends Content> extends BluetoothTask{
    private String TAG = "BluetoothReceiveTask";
    private ReceiveCompleteCallback callback;
    private Class<HeaderType> headerTypeClass;
    private Class<ContentType> contentClass;
    public BluetoothReceiveTask(BluetoothSocket socket, ReceiveCompleteCallback callback, Class<HeaderType> headerTypeClass, Class<ContentType> contentClass) {
        super(socket);
        this.callback = callback;
        this.headerTypeClass = headerTypeClass;
        this.contentClass = contentClass;
    }

    @Override
    public void run(){
        HeaderType header;
        ContentType content;

        try {
            header = this.headerTypeClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e(TAG,"Error occurred when creating an instance of header",e);
            return;
        }
        try {
            content = this.contentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e(TAG,"Error occurred when creating an instance of content",e);
            return;
        }
        header.fromInputStream(this.inputStream);
        content.fromInputStream(header, inputStream);
        this.callback.receiveComplete(content.convertToString());
    }
}
