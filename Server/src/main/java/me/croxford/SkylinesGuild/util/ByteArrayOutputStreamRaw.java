package me.croxford.SkylinesGuild.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public final class ByteArrayOutputStreamRaw extends ByteArrayOutputStream {

    public ByteArrayOutputStreamRaw(int size) {
        super(size);
    }
    public InputStream readInputStream() {
        return new ByteArrayInputStream(this.buf);
    }
}
