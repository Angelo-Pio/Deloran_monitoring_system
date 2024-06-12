package com.tlc.deloran.repository;


import com.tlc.deloran.model.Gateway;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntRepository extends MongoRepository<Gateway,String> {

}

