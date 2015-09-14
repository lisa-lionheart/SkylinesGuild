package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.City;
import me.croxford.SkylinesGuild.model.CityRepository;
import me.croxford.SkylinesGuild.model.ModInfo;
import me.croxford.SkylinesGuild.model.SaveGame;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Date;

/**
 * Created by Lisa on 10/09/2015.
 */
public class TestSaveGame {

    @Autowired
    CityRepository repository;


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
//
//        SaveGame saveGame = new SaveGame(new Date());
//
//        byte[] data = Files.readAllBytes(FileSystems.getDefault().getPath("testdata","thumbnail.png"));
//        saveGame.storeThumbNailData(data);
    }

    @Test
    public void testStoreModinfo() {


    }
}
