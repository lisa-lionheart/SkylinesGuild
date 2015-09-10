package me.croxford.SkylinesGuild.controller;

import me.croxford.SkylinesGuild.model.User;
import me.croxford.SkylinesGuild.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by Lisa on 10/09/2015.
 */
public class BaseController {


    @Autowired
    protected UserRepository users;


    public User getCurrentUser() {

        Object details = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(details instanceof User) {
            return (User)details;
        }

        return null;
    }
}