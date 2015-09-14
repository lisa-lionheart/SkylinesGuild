package me.croxford.SkylinesGuild.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by Lisa on 13/09/2015.
 */
public class DataStreamEx extends DataInputStream {

    public DataStreamEx(InputStream in) { super(in); }

    public String readString() throws IOException {
        int length = readUnsignedByte();
        byte[] data = new byte[length];
        read(data, 0, length);
        return new String(data, Charset.defaultCharset());
    }

    public InputStream readFile() throws IOException {


        int length = readInt();

        File f = File.createTempFile("temp", "tmp");
        f.deleteOnExit();

        System.out.format("Temp file %s", f.toString());

        FileOutputStream file = new FileOutputStream(f);

        byte[] buffer = new byte[4*1024];
        int bytesRmaining = length;
        while(bytesRmaining > 0) {
            int read = read(buffer, 0, bytesRmaining > buffer.length ? buffer.length : bytesRmaining);
            file.write(buffer,0,read);
            bytesRmaining -= read;
        }

        return new MarkableFileInputStream(new TempFileInputStream(f));
    }

}
