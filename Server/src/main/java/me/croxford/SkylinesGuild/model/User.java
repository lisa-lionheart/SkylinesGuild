package me.croxford.SkylinesGuild.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamId;
import me.croxford.SkylinesGuild.ClientConnection;
import me.croxford.SkylinesGuild.ClientConnectionManager;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public class User implements UserDetails {
    @Id
    private String userId;
    private String openId;
    private String clientSecret;

    public User(String openId) throws UsernameNotFoundException {
        this.setClientSecret("");
        this.setOpenId(openId);
    }


    public long getSteamId() {
        String idPart = getOpenId().substring(getOpenId().lastIndexOf('/') + 1);
        return Long.parseUnsignedLong(idPart);
    }


    @JsonIgnore
    private SteamId getSteamProfile() {
        try {
            return SteamId.create(getSteamId(), true);
        } catch (SteamCondenserException e) {
            throw new UsernameNotFoundException(getOpenId());
        }
    }

    public String getNickName() { return getSteamProfile().getNickname(); }

    public String getAvatarUrl() {
        return getSteamProfile().getAvatarIconUrl();
    }

    public String getOnlineState() {
        return getSteamProfile().getStateMessage();
    }

    public boolean isConnected() {
        return  getClientConnection() != null;

    }


    @JsonIgnore
    public ClientConnection getClientConnection() {
        return ClientConnectionManager.getInstance().getConnection(getClientSecret());
    }

    //User detail methods

    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public String getUsername() {
        return getOpenId();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }


    @Override
    @JsonIgnore
    public String getPassword() {
        return null;
    }


    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @JsonIgnore
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
