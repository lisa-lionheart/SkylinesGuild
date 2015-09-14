package me.croxford.SkylinesGuild.model;

import org.springframework.data.annotation.Id;

/**
 * Created by Lisa on 13/09/2015.
 */
public class ModInfo {

    String id;
    String modName;

    boolean clientOnly;

    public ModInfo() {

    }

    public ModInfo(String modId, String modName) {
        this.id = modId;
        this.modName = modName;

    }

    public String getModName() {
        return modName;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }


    public boolean isClientOnly() {
        return clientOnly;
    }

    public void setClientOnly(boolean clientOnly) {
        this.clientOnly = clientOnly;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
