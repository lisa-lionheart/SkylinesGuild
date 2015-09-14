package me.croxford.SkylinesGuild.controller;

import me.croxford.SkylinesGuild.Application;
import me.croxford.SkylinesGuild.model.User;
import me.croxford.SkylinesGuild.model.UserRepository;
import org.bson.types.ObjectId;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Properties;

/**
 * Created by Lisa on 10/09/2015.
 */
public class BaseController {


    @Autowired
    protected UserRepository users;

    @Value("${SkylinesGuild.disableAuth}")
    private boolean disableAuth;

    public User getCurrentUser() {

        if(disableAuth) {
            return users.findById(new ObjectId("55f18f443b62d9124eaa3fdf"));
        }

        Object details = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(details instanceof User) {
            return (User)details;
        }

        return null;

    }
}
