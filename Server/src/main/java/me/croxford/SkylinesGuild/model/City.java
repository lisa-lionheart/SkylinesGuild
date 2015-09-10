package me.croxford.SkylinesGuild.model;



import org.springframework.data.annotation.Id;

import java.util.ArrayList;

public class City {


    @Id
    private String cityId;
    private String cityName;
    private String ownerId;

    private ArrayList<SaveGame> saveGames;

    public City() {
    }

    /**
     * Initialize new city with no parameters
     * @param cityName
     * @param ownerId
     */
    public City(String cityName, String ownerId) {

        this.cityName = cityName;
        this.ownerId = ownerId;
        this.saveGames = new ArrayList<SaveGame>();
    }


    public void addSaveGame(SaveGame save) {
        saveGames.add(save);
    }

    public SaveGame getLastSave() {
        return saveGames.get(saveGames.size()-1);
    }

    //Generic getters and setters;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public ArrayList<SaveGame> getSaveGames() {
        return saveGames;
    }

    public void setSaveGames(ArrayList<SaveGame> saveGames) {
        this.saveGames = saveGames;
    }

}
