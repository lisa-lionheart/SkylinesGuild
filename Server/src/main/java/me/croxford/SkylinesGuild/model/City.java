package me.croxford.SkylinesGuild.model;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class City {



    @Id
    private ObjectId id;
    private String cityName;

    @DBRef(lazy = true)
    private User owner;

    private ArrayList<ModInfo> usedMods;

    public User getCurrentMayor() {
        return currentMayor;
    }

    public void setCurrentMayor(User currentMayor) {
        this.currentMayor = currentMayor;
    }

    @DBRef
    private User currentMayor;

    private ArrayList<SaveGame> saveGames;

    public City() {
    }

    /**
     * Initialize new city with no parameters
     * @param cityName
     * @param owner
     */
    public City(String cityName, User owner) {

        this.cityName = cityName;
        this.owner = owner;
        this.saveGames = new ArrayList<SaveGame>();
        this.usedMods = new ArrayList<ModInfo>();
    }

    public void addSaveGame(SaveGame save) {
        saveGames.add(save);
    }

    public SaveGame getLastSave() {
        return saveGames.get(saveGames.size()-1);
    }

    //Generic getters and setters;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Transient
    public String getCityId() {
        return id.toString();
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public ObjectId getId() {
        return id;
    }

    @JsonIgnore
    public void setId(ObjectId id) {
        this.id = id;
    }

    public ArrayList<SaveGame> getSaveGames() {
        return saveGames;
    }

    @JsonIgnore
    public void setSaveGames(ArrayList<SaveGame> saveGames) {
        this.saveGames = saveGames;
    }

    public boolean canUpdate(User savingUser) {

        //TODO: Implement me
        Logger.getGlobal().severe("No authentication, anyone can modify this city!!!");
        return true;
    }

    public ArrayList<ModInfo> getUsedMods() {
        return usedMods;
    }

    public void setUsedMods(ArrayList<ModInfo> usedMods) {
        this.usedMods = usedMods;
    }
}
