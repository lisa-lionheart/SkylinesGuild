package me.croxford.SkylinesGuild.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRepository extends MongoRepository<City, String> {

    City findById(ObjectId cityId);
    List<City> findByOwner(ObjectId ownerId);

}
