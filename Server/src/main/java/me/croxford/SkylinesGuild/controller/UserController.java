package me.croxford.SkylinesGuild.controller;

import me.croxford.SkylinesGuild.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends BaseController {


    @RequestMapping("/user")
    public User currentUser() {
        return getCurrentUser();
    }

    @RequestMapping("/user/{userId}")
    public User getUser(@PathVariable("userId") String userId) {
        return users.findByUserId(userId);
    }
}
