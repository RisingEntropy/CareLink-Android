package edu.uestc.carelink.communication.protocol.header;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Header {
    private int content_length;
    private int version;
    private Map<String, String> extra;
    public Header(){
        this.extra = new HashMap<>();
    }


    public String getExtra(String key){
        return this.extra.get(key);
    }
    public void putExtra(String key, String value){
        this.extra.put(key, value);
    }


    public abstract String convertToString();
    public abstract void fromInputStream(InputStream stream);

    public int getContent_length() {
        return content_length;
    }

    public void setContent_length(int content_length) {
        this.content_length = content_length;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
