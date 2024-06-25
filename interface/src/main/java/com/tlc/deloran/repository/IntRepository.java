package com.tlc.deloran.repository;


import com.tlc.deloran.model.Resources;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntRepository extends MongoRepository<Resources,String> {

    @Query("{timestamp: {$gte: ?0, $lte: ?1 }}")
    List<Resources> findAllByTimestamp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    List<Resources> findAllByTimestampBefore(LocalDateTime end);

    @Query("{$and: [{timestamp: {$lte: ?1 }},{ id: {$in: ?0}}] }")
    List<Resources> findAllById_TimestampBefore(@Param("id") String[] id, @Param("end") LocalDateTime end);

    @Query("{$and: [{timestamp: {$gte: ?1, $lte: ?2 } } ,{id: {$in: ?0 } } ] }")
    List<Resources> findAllById_Timestamp(@Param("id")String[] id, @Param("start")LocalDateTime start, @Param("end")LocalDateTime end);


}

