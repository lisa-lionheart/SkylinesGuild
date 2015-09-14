package me.croxford.SkylinesGuild.model;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.croxford.SkylinesGuild.util.ByteArrayOutputStreamRaw;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpStatus;
import org.itadaki.bzip2.BZip2OutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.beans.Transient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by Lisa on 10/09/2015.
 */
public class SaveGame {



    class CustomAssetInfo {

    }


    static String bucketName = "guild-objects";
    static String downloadPrefix = "http://guild-objects.s3.amazonaws.com/";

    private Date timestamp;
    private Date inGameDate;

    private String saveFileId;
    private String thumbnailId;
    private String cityName;

    //@DBRef(lazy = true)
    private List<ModInfo> activeMods;
    //@DBRef(lazy = true)
    private List<CustomAssetInfo> customAssets;

    private InputStream saveData, thumbnailData;

    @DBRef(lazy=true)
    private User user;
    private int population;
    private long cash;
    private long cashDelta;

    public SaveGame() {
    }

    private String storeObject(InputStream is, String extension, boolean compress) throws IOException {
        AmazonS3Client s3 = new AmazonS3Client();


        is.mark(is.available());
        String md5 = DigestUtils.md5Hex(is);
        is.reset();


        String keyName = md5 + "." + extension;
        boolean alreadyExists = true;
        try {
            s3.getObjectMetadata(bucketName, keyName);

        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                alreadyExists = false;
            }else{
                throw e;
            }
        }

        if(alreadyExists) {
            return md5;
        }


        ObjectMetadata meta = new ObjectMetadata();
        if(extension.equals("png")) {
            meta.setContentType("image/png");
        }
        meta.setCacheControl("public,maxage=600");


        if(compress) {

            //Expect about 1/2 to 2/3 compression with bzip
            ByteArrayOutputStreamRaw compressData = new ByteArrayOutputStreamRaw((is.available()*2)/3);
            BZip2OutputStream compressStream = new BZip2OutputStream(compressData);
            IOUtils.copy(is, compressStream);
            compressStream.finish();

            is = compressData.readInputStream();
            meta.setContentLength(compressData.size());
        }else {
            meta.setContentLength(is.available());
        }

        s3.putObject(bucketName, keyName, is, meta);
        s3.setObjectAcl(bucketName,keyName, CannedAccessControlList.PublicRead);
        return md5;
    }

    // Commit data to S3
    public void commitData() throws IOException {
        saveFileId = storeObject(saveData, "crp.bzip2", true);
        thumbnailId = storeObject(thumbnailData, "png",false);

        saveData.close();
        thumbnailData.close();;

        saveData = null;
        thumbnailData = null;
    }


    public String getDownloadUrl() {
        return downloadPrefix + saveFileId + ".crp.bzip2";
    }

    public String getThumbnailUrl() {
        return downloadPrefix + thumbnailId + ".png";
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setSaveFileId(String val) {
        saveFileId = val;
    }

    public String getSaveFileId() {
        return saveFileId;
    }

    public void setThumbnailId(String val) {
        thumbnailId = val;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getInGameDate() {
        return inGameDate;
    }

    public void setInGameDate(Date inGameDate) {
        this.inGameDate = inGameDate;
    }


    public long getCashDelta() {
        return cashDelta;
    }

    public void setCashDelta(long cashDelta) {
        this.cashDelta = cashDelta;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<ModInfo> getActiveMods() {
        return activeMods;
    }

    public void setActiveMods(List<ModInfo> activeMods) {
        this.activeMods = activeMods;
    }

    public List<CustomAssetInfo> getCustomAssets() {
        return customAssets;
    }

    public void setCustomAssets(List<CustomAssetInfo> customAssets) {
        this.customAssets = customAssets;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Transient
    @JsonIgnore
    public InputStream getSaveData() {
        return saveData;
    }

    @Transient
    @JsonIgnore
    public void setSaveData(InputStream saveData) {
        this.saveData = saveData;
    }


    @Transient
    @JsonIgnore
    public InputStream getThumbnailData() {
        return thumbnailData;
    }


    @Transient
    @JsonIgnore
    public void setThumbnailData(InputStream thumbnailData) {
        this.thumbnailData = thumbnailData;
    }
}
