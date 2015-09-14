package me.croxford.SkylinesGuild.util;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Lisa on 13/09/2015.
 */
public class MarkableFileInputStream extends FilterInputStream {
    private FileChannel myFileChannel;
    private long mark = -1;

    public MarkableFileInputStream(FileInputStream fis) {
        super(fis);
        myFileChannel = fis.getChannel();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            mark = myFileChannel.position();
        } catch (IOException ex) {
            mark = -1;
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (mark == -1) {
            throw new IOException("not marked");
        }
        myFileChannel.position(mark);
    }
}