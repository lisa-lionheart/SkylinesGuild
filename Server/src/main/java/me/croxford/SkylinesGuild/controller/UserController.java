package me.croxford.SkylinesGuild.controller;

import me.croxford.SkylinesGuild.*;
import me.croxford.SkylinesGuild.model.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class UserController extends BaseController {

    @Autowired
    CityRepository cities;

    @RequestMapping("/user")
    public User currentUser() {
        return getCurrentUser();
    }

    @RequestMapping("/user/{userId}")
    public User getUser(@PathVariable("userId") String userId) {
        return users.findById(new ObjectId(userId));
    }

    @RequestMapping("/user/{userId}/cities")
    public List<City> getUserCities(@PathVariable("userId") String userId) {
        return cities.findByOwner(new ObjectId(userId));
    }

    @RequestMapping("/connect/{clientSecret}")
    public void connectClient(@PathVariable("clientSecret") String clientSecret, HttpServletResponse httpServletResponse) throws IOException {

        ClientConnection client =  ClientConnectionManager.getInstance().getConnection(clientSecret);
        if(client != null) {
            User user = getCurrentUser();
            user.setClientSecret(clientSecret);
            users.save(user);
        } else {
            throw new Error("Client not connected: " + clientSecret);
        }

        httpServletResponse.sendRedirect("/");
    }
}
