package me.croxford.SkylinesGuild;

import junit.framework.TestCase;
import junit.framework.TestResult;

import java.io.*;

/**
 * Created by Lisa on 11/09/2015.
 */
public class SaveGameUploadParserTest extends TestCase {



    public void testCanParseSavegame() throws IOException {

        InputStream is = null;
        try {
            is = new FileInputStream("testdata/upload.bin");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SaveGameUploadParser parser = new SaveGameUploadParser(is);

        FileOutputStream out = new FileOutputStream("test.png");
        out.write(parser.getThumbnail());

        assertEquals(parser.getCityName(), "Springdale");
        assertEquals(parser.getPopulation(), 628);
        assertEquals(parser.getCash(), 55152746);


    }
}