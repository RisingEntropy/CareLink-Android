package edu.uestc.carelink.communication.protocol.header;

import android.annotation.SuppressLint;

import java.io.InputStream;
import java.util.Scanner;

import edu.uestc.carelink.communication.protocol.content.PlainContent;

/*
* This class is mainly used for debugging
* */
public class PlainHeader extends Header{
    private int content_length;
    private int version;
    public PlainHeader(int content_length){
        this.content_length = content_length;
        this.version = 0;
    }
    @SuppressLint("DefaultLocale")
    @Override
    public String convertToString() {
        return String.format("%d %d", content_length, version);
    }

    @Override
    public void fromInputStream(InputStream stream) {
        Scanner scan = new Scanner(stream);
        this.setContent_length(scan.nextInt());
        this.setVersion(scan.nextInt());
    }
}
