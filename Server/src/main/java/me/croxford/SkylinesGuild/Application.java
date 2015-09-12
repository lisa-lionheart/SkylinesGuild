package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.City;

import me.croxford.SkylinesGuild.model.CityRepository;
import me.croxford.SkylinesGuild.model.SaveGame;
import me.croxford.SkylinesGuild.model.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {




    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }


    public static void main(String[] args) {


        SpringApplication.run(Application.class, args);
    }

 }
