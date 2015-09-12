package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.City;
import me.croxford.SkylinesGuild.model.CityRepository;
import me.croxford.SkylinesGuild.model.SaveGame;
import me.croxford.SkylinesGuild.model.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * Created by Lisa on 11/09/2015.
 */

@Component
public class TestData {

    @Autowired
    private CityRepository cities;
    @Autowired
    private UserRepository users;


    @PostConstruct
    void init() {
        /*
        cities.deleteAll();

        City city = new City("Steamboat springs", users.findById(new ObjectId("55f18f443b62d9124eaa3fdf")));

        SaveGame save = new SaveGame(new Date());

        save.setThumbnailId("abebb9f8b479f0b4bd0d46c923ce1778");
        save.setSaveFileId("d10ef2b4bfb9526455002dd280a0e229");
//        save.storeSaveGameData(Files.readAllBytes(FileSystems.getDefault().getPath("testdata","NewSave.crp")));

        city.addSaveGame(save);
        cities.save(city);*/
    }
}
