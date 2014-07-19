package io.divide.shared.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by williamwebb on 7/17/14.
 */
public class IOUtils {

    public static String toString(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }

    public static InputStream toInputStream(String input, String encoding) throws IOException {
        byte[] bytes = input.getBytes(Charset.forName(encoding));
        return new ByteArrayInputStream(bytes);
    }

}
