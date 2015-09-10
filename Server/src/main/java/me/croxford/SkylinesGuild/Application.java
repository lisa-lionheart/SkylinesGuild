package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.City;

import me.croxford.SkylinesGuild.model.CityRepository;
import me.croxford.SkylinesGuild.model.SaveGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class Application implements CommandLineRunner {


    @Autowired
    private CityRepository cities;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        cities.deleteAll();


        City city = new City("Steamboat springs", "55f18f443b62d9124eaa3fdf");

        SaveGame save = new SaveGame(new Date());

        save.setThumbnailId("abebb9f8b479f0b4bd0d46c923ce1778");
        save.setSaveFileId("d10ef2b4bfb9526455002dd280a0e229");
//        save.storeSaveGameData(Files.readAllBytes(FileSystems.getDefault().getPath("testdata","NewSave.crp")));

        city.addSaveGame(save);
        cities.save(city);

    }
}
