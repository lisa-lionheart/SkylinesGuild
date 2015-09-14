package me.croxford.SkylinesGuild.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.beans.Transient;
import java.util.Collection;

/**
 * Created by Lisa on 12/09/2015.
 */
public class OpenIDUser implements UserDetails {
    @Id
    protected ObjectId id;
    @Indexed(unique = true)
    protected String openId;

    //User detail methods
    @Transient
    public boolean isLoggedIn() {
        return openId != null;
    }

    @Override
    @Transient
    public String getUsername() {
        return getOpenId();
    }

    @Override
    @JsonIgnore
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    @Transient
    public String getPassword() {
        return null;
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    @Transient
    public boolean isEnabled() {
        return true;
    }

    public String getOpenId() {
        return openId;
    }
    public void setOpenId(String openId) {
        this.openId = openId;
    }

}
