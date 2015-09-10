package me.croxford.SkylinesGuild.model;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by Lisa on 10/09/2015.
 */
public class SaveGame {


    static String bucketName = "guild-objects";
    static String downloadPrefix = "http://guild-objects.s3.amazonaws.com/";

    private Date timestamp;

    private String saveFileId;
    private String thumbnailId;


    private int population;
    private int balance;

    public SaveGame() {
    }

    public SaveGame(Date timestamp) {
        this.timestamp = timestamp;
        this.saveFileId = saveFileId;
        this.thumbnailId = thumbnailId;
    }


    private String storeObject(byte[] data, String extension) {
        AmazonS3Client s3 = new AmazonS3Client();

        String md5 = null;
        try {
            byte[] digest = MessageDigest.getInstance("md5").digest(data);
            md5 = new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ObjectMetadata meta = new ObjectMetadata();
        if(extension.equals("png")) {
            meta.setContentType("image/png");
        }
        meta.setCacheControl("public,maxage=600");
        ByteArrayInputStream is = new ByteArrayInputStream(data);

        String keyName = md5 + "." + extension;
        s3.putObject(bucketName, keyName, is, meta);
        s3.setObjectAcl(bucketName,keyName, CannedAccessControlList.PublicRead);
        return md5;
    }

    public void storeSaveGameData(byte[] data) {
        saveFileId = storeObject(data, "crp");
    }

    public void storeThumbNailData(byte[] data) {
        thumbnailId = storeObject(data, "png");
    }


    public String getDownloadUrl() {
        return downloadPrefix + saveFileId + ".crp";
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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
