package me.croxford.SkylinesGuild.util;

import java.io.*;
import java.nio.file.Path;

/**
 * Created by Lisa on 13/09/2015.
 */
public class TempFileInputStream extends FileInputStream {

    private File theFile;

    public TempFileInputStream(File file) throws FileNotFoundException {
        super(file);
        theFile = file;
    }

    @Override
    public void close() throws IOException {
        theFile.delete();
        super.close();
    }
}
