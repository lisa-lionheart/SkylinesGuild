package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lisa on 11/09/2015.
 */

@Component
public class TestData {

    @Autowired
    private CityRepository cities;
    @Autowired
    private UserRepository users;
    @Autowired
    private ModInfoRepository mods;


    @PostConstruct
    void init() {

        cities.deleteAll();

        City city = new City("Steamboat springs", users.findById(new ObjectId("55f18f443b62d9124eaa3fdf")));


        SaveGameMessageConvertor parse = new SaveGameMessageConvertor();
        SaveGame save = null;
        try {
            save = parse.read(SaveGame.class, new HttpInputMessage() {
                @Override
                public InputStream getBody() throws IOException {
                    return new FileInputStream("testdata/upload.bin");
                }

                @Override
                public HttpHeaders getHeaders() {
                    return null;
                }
            });

            save.commitData();
            city.addSaveGame(save);
        } catch (IOException e) {
            e.printStackTrace();
        }


        city.getUsedMods().add(new ModInfo("TrafficReport", "Traffic Report Tool 1.5"));
        city.getUsedMods().add(new ModInfo("Traffic++", "Traffic++"));


        mods.save(city.getUsedMods());


        city.addSaveGame(save);
        cities.save(city);
    }
}
