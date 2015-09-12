package me.croxford.SkylinesGuild.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Lisa on 10/09/2015.
 */
public interface UserRepository extends MongoRepository<User, String> {

    User findByOpenId(String cityId);
    User findById(ObjectId userId);
}
