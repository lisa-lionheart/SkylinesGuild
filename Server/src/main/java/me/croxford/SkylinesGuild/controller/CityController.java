package me.croxford.SkylinesGuild.controller;

import me.croxford.SkylinesGuild.ClientConnection;
import me.croxford.SkylinesGuild.model.City;
import me.croxford.SkylinesGuild.model.CityRepository;
import me.croxford.SkylinesGuild.model.SaveGame;
import me.croxford.SkylinesGuild.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;


@RestController
public class CityController extends BaseController {

    @Autowired
    private CityRepository cities;

    @RequestMapping(value = "/city/all", method = RequestMethod.GET)
    public List<City> getAllCities() {
        return cities.findAll();
    }

    @RequestMapping(value = "/city/{cityId}" ,produces = "application/json")
    public City getCity(@PathVariable("cityId") String cityId) {

        return cities.findById(new ObjectId(cityId));
    }

    @RequestMapping(value="/city/{cityId}/play", method = RequestMethod.POST)
    public boolean playCity(@PathVariable("cityId") String cityId) {


        City city =  cities.findById(new ObjectId(cityId));
        if(city == null) {
            return false;
        }

        ClientConnection client = getCurrentUser().getClientConnection();
        if(null != client) {
            client.playCity(city, city.getLastSave());
        }

        return false;
    }

    @RequestMapping(value="/city/{cityId}", method = RequestMethod.POST)
    public ResponseEntity publishSave(
            @PathVariable("cityId") ObjectId id, @RequestBody SaveGame newSave
    ) throws IOException {

        City city = cities.findById(id);
        if(city == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        User savingUser = getCurrentUser();
        if(!city.canUpdate(savingUser)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        newSave.setUser(savingUser);

        city.addSaveGame(newSave);

        cities.save(city);

        newSave.commitData();

        return new ResponseEntity(HttpStatus.ACCEPTED);
    };
}
