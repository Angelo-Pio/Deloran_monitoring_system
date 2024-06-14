package com.tlc.deloran.repository;


import com.tlc.deloran.model.Gateway;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntRepository extends MongoRepository<Gateway,String> {

    @Query("{timestamp: {$gte: :#{#start}, $lt: :#{#end} }}")
    List<Gateway> findAllByTimestamp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    List<Gateway> findAllByTimestampBefore(LocalDateTime end);
}

