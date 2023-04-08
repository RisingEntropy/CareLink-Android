package edu.uestc.carelink.communication.protocol.content;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import edu.uestc.carelink.communication.protocol.header.Header;
import edu.uestc.carelink.communication.protocol.header.PlainHeader;

public class PlainContent extends Content{
    private static final String TAG = "PlainContent";
    private String message;
    public PlainContent(){

    }
    public PlainContent(String message){
        this.message = message;
    }

    @Override
    public String convertToString() {
        return message;
    }

    @Override
    public void fromInputStream(Header header, InputStream stream) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            try {
                byte[] buffer = stream.readNBytes(header.getContent_length());
                this.message = new String(buffer);
            } catch (IOException e) {
                Log.e(TAG,"error occurred when constructing a PlainContent from InputStream",e);
                this.message = null;
            }

        }
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
