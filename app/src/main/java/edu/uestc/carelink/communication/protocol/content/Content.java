package edu.uestc.carelink.communication.protocol.content;

import java.io.InputStream;

import edu.uestc.carelink.communication.protocol.header.Header;

public abstract class Content {
    public abstract String convertToString();
    public abstract void fromInputStream(Header header, InputStream stream);
}
