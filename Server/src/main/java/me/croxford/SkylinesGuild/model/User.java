package me.croxford.SkylinesGuild.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamId;
import me.croxford.SkylinesGuild.ClientConnection;
import me.croxford.SkylinesGuild.ClientConnectionManager;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.beans.Transient;
import java.util.Collection;

public class User extends OpenIDUser {

    private String clientSecret;

    public User(String openId) throws UsernameNotFoundException {
        this.setClientSecret("");
        this.setOpenId(openId);
    }

    @Transient
    public long getSteamId() {
        String idPart = getOpenId().substring(getOpenId().lastIndexOf('/') + 1);
        return Long.parseUnsignedLong(idPart);
    }


    @JsonIgnore
    @Transient
    private SteamId getSteamProfile() {
        try {
            return SteamId.create(getSteamId(), true);
        } catch (SteamCondenserException e) {
            throw new UsernameNotFoundException(getOpenId());
        }
    }

    @Transient
    public String getNickName() { return getSteamProfile().getNickname(); }

    @Transient
    public String getAvatarUrl() {
        return getSteamProfile().getAvatarIconUrl();
    }

    @Transient
    public String getOnlineState() {
        return getSteamProfile().getStateMessage();
    }

    @Transient
    public boolean isConnected() {
        return  getClientConnection() != null;
    }


    @JsonIgnore
    @Transient
    public ClientConnection getClientConnection() {
        return ClientConnectionManager.getInstance().getConnection(getClientSecret());
    }


    @JsonIgnore
    public ObjectId getId() {
        return id;
    }

    @JsonIgnore
    public void setId(ObjectId id) {
        this.id = id;
    }

    @JsonProperty("id")
    @Transient
    public String getIdAsString() { return this.id.toString(); }


    @JsonIgnore
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
