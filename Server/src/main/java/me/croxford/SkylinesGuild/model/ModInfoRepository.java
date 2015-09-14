package me.croxford.SkylinesGuild.model;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Lisa on 13/09/2015.
 */
public interface ModInfoRepository extends MongoRepository<ModInfo,String> {
    ModInfo findById(String id);
}

