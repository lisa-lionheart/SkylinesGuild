package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.ModInfoRepository;
import me.croxford.SkylinesGuild.model.SaveGame;
import me.croxford.SkylinesGuild.util.DataStreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class SaveGameMessageConvertor extends AbstractHttpMessageConverter<SaveGame> {

    static byte formatVersion = 2;

    @Autowired
    private ModInfoRepository mods;

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @PostConstruct
    public void register() {
        List<HttpMessageConverter<?>> messageConverters = adapter.getMessageConverters();
        messageConverters.add(0, this);
    }

    SaveGameMessageConvertor() {
        super(MediaType.ALL);
    }

    @Override
    protected boolean supports(Class clazz) {
        return SaveGame.class == clazz;
    }

    @Override
    protected SaveGame readInternal(Class<? extends SaveGame> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        SaveGame save = new SaveGame();

        DataStreamEx dataStream = new DataStreamEx(inputMessage.getBody());

        byte version = dataStream.readByte();
        if(version != formatVersion) {
            throw new HttpMessageNotReadableException("Incorrect version number");
        }

        save.setCityName(dataStream.readString());

        save.setCash(dataStream.readLong());
        save.setCashDelta(dataStream.readLong());
        save.setPopulation(dataStream.readInt());

        save.setInGameDate(new Date((long)(dataStream.readDouble()*1000)));

        save.setThumbnailData(dataStream.readFile());
        save.setSaveData(dataStream.readFile());
        return save;
    }

    @Override
    protected void writeInternal(SaveGame saveGame, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

    }
}
