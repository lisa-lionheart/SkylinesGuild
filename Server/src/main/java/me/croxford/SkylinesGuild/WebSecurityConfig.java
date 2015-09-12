package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.User;
import me.croxford.SkylinesGuild.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements AuthenticationManager, UserDetailsService {

    @Autowired
    private UserRepository users;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();


//
//        http
//            .authorizeRequests()
//                .antMatchers("/stylesheets/","/**", "/js/**", "/partials/**") .permitAll()
//                .antMatchers("/**").authenticated();
//
//
//            http
//                    .openidLogin()
//                    .loginPage("/login")
//                    .defaultSuccessUrl("/", true)
//                    .permitAll();

        http.rememberMe();
    }

    @Override
    protected void configure(  AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(this);
        auth.userDetailsService(this);

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authentication;
    }

    @Override
    public UserDetails loadUserByUsername(String openId) throws UsernameNotFoundException {

        User user = users.findByOpenId(openId);
        if(user != null) {
            return user;
        }

        user = new User(openId);
        users.save(user);
        return user;
    }
}