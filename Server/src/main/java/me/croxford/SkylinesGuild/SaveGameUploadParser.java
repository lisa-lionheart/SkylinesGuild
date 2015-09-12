package me.croxford.SkylinesGuild;

import java.util.Date;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Lisa on 11/09/2015.
 */
public class SaveGameUploadParser {

    static byte formatVersion = 2;

    private String cityName;
    private int population;


    private long cash, cashDelta;
    private Date inGameTime;

    private byte[] thumbnail;
    private byte[] savegame;

    DataInputStream dataStream;

    public SaveGameUploadParser(InputStream stream) throws IOException {
        dataStream = new DataInputStream(stream);

        byte version = dataStream.readByte();
        if(version != formatVersion) {
            throw new IOException("Incorrect version");
        }

        cityName = readString();

        cash = dataStream.readLong();
        cashDelta = dataStream.readLong();
        population = dataStream.readInt();

        inGameTime = new Date((long)(dataStream.readDouble()*1000));

        thumbnail = readFile();
        savegame = readFile();

    }

    private String readString() throws IOException {
        int length = dataStream.readUnsignedByte();
        byte[] data = new byte[length];
        dataStream.read(data, 0, length);
        return new String(data, Charset.defaultCharset());
    }

    private byte[] readFile() throws IOException {
        int length = dataStream.readInt();
        byte[] data = new byte[length];
        dataStream.readFully(data, 0, length);
        return data;
    }

    public String getCityName() {
        return cityName;
    }

    public int getPopulation() {
        return population;
    }
    public byte[] getThumbnail() {
        return thumbnail;
    }

    public byte[] getSavegame() {
        return savegame;
    }

    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public long getCashDelta() {
        return cashDelta;
    }

    public void setCashDelta(long cashDelta) {
        this.cashDelta = cashDelta;
    }

    public Date getInGameTime() {
        return inGameTime;
    }
}
