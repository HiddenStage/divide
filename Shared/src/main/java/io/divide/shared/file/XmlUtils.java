package io.divide.shared.file;

import com.thoughtworks.xstream.XStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class XmlUtils
{
    static XStream xStream = new XStream();

    public static void writeMapXml(Map<?, ?> mapToWriteToDisk, OutputStream str) throws IOException {
        xStream.toXML(mapToWriteToDisk,str);
    }

    public static Map readMapXml(InputStream str) {
        try {
            return (Map) xStream.fromXML(str);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}