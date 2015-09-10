import me.croxford.SkylinesGuild.model.SaveGame;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Date;

/**
 * Created by Lisa on 10/09/2015.
 */
public class TestSaveGame {


//
//    @Test
//    public void testStoreSaveGameData() throws IOException {
//
//        SaveGame saveGame = new SaveGame(new Date());
//
//        byte[] data = Files.readAllBytes(FileSystems.getDefault().getPath("testdata","NewSave.crp"));
//        saveGame.storeSaveGameData(data);
//    }



    @Test
    public void testStoreThumbNailData() throws IOException {

        SaveGame saveGame = new SaveGame(new Date());

        byte[] data = Files.readAllBytes(FileSystems.getDefault().getPath("testdata","thumbnail.png"));
        saveGame.storeThumbNailData(data);
    }
}
