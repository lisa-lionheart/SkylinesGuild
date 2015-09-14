package me.croxford.SkylinesGuild;

import junit.framework.TestCase;
import me.croxford.SkylinesGuild.model.SaveGame;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.*;

/**
 * Created by Lisa on 11/09/2015.
 */
public class SaveGameUploadParserTest extends TestCase {



    public void testCanParseSavegame() throws IOException {

        final InputStream is = new FileInputStream("testdata/upload.bin");

        SaveGameMessageConvertor parser = new SaveGameMessageConvertor();

        SaveGame save = parser.read(SaveGame.class, new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return is;
            }

            @Override
            public HttpHeaders getHeaders() {
                return null;
            }
        });

        String url = save.getThumbnailUrl();

        save.commitData();

        assertEquals("Springdale",save.getCityName());
        assertEquals(628,save.getPopulation());
        assertEquals(55163856, save.getCash());


    }
}