package me.croxford.SkylinesGuild.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRepository extends MongoRepository<City, String> {

    City findByCityId(String cityId);
    List<City> findByOwnerId(String ownerId);

}
