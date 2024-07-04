package com.tlc.deloran.repository;

import com.tlc.deloran.model.Packet;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PacketsRepository extends MongoRepository<Packet,String> {

    @Query("{timestamp: {$gte: ?0}}")
    List<Packet> findAllByTimestamp(@Param("start") LocalDateTime start);
}
