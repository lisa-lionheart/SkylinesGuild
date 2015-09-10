package me.croxford.SkylinesGuild.controller;

import me.croxford.SkylinesGuild.ClientConnection;
import me.croxford.SkylinesGuild.model.City;
import me.croxford.SkylinesGuild.model.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class CityController extends BaseController {

    @Autowired
    private CityRepository cities;


    @RequestMapping(value = "/city/all", method = RequestMethod.GET)
    public List<City> getAllCities() {
        return cities.findAll();
    }

    @RequestMapping("/city/{cityId}")
    public City getCity(@PathVariable("cityId") String cityId) {

        return cities.findByCityId(cityId);
    }

    @RequestMapping("/city/{cityId}/play")
    public boolean playCity(@PathVariable("cityId") String cityId) {


        City city =  cities.findByCityId(cityId);
        if(city == null) {
            return false;
        }

        ClientConnection client = getCurrentUser().getClientConnection();
        if(null != client) {
            client.playCity(city, city.getLastSave());
        }

        return false;
    }
}
