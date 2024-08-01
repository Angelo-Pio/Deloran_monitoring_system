package com.tlc.deloran.service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoClient;
import com.tlc.deloran.model.Resources;
import com.tlc.deloran.repository.ResoucesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class ResourcesService {

    @Autowired
    private ResoucesRepository resoucesRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoClient mongo;

    public List<Resources> getAllResources(){
        return resoucesRepository.findAll();
    }

    // Main method
    public List<Resources> getAllByIdAndTimestamp(String[] id, LocalDateTime start, LocalDateTime end){


       /* start = start != null ? toUTC(start) : null;
        end = end != null ? toUTC(end) : null;*/
        List<Resources> gateways = new LinkedList<>();
        if(id == null){
            gateways = getAllResourcesByTimestamp(start, end);
        } else if (id.length == 0) {
            gateways = getAllResourcesByTimestamp(start, end);
        } else{
            gateways =  getAllResourcesById(id,start,end);
        }
        return gateways;
    }

    public List<Resources> getAllResourcesByTimestamp(LocalDateTime start, LocalDateTime end){

//        If both null give last hour info
//        if end null give all from start to now
//        if start null give all until end

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last5Minutes = now.minusMinutes(5);
        if(start == null && end == null){
            return resoucesRepository.findAllByTimestamp(last5Minutes, now);
        } else if (start != null && end == null) {
            return resoucesRepository.findAllByTimestamp(start, now);
        }else if(start == null && end != null){
            return resoucesRepository.findAllByTimestampBefore(end);
        }
        else{
            return resoucesRepository.findAllByTimestamp(start, end);
        }
    }




    private List<Resources> getAllResourcesById(String[] id, LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last5Minutes = now.minusMinutes(5);
        if(start == null && end == null){
            return resoucesRepository.findAllById_Timestamp(id,last5Minutes, now);
        } else if (start != null && end == null) {
            return resoucesRepository.findAllById_Timestamp(id,start, now);
        }else if(start == null && end != null){
            return resoucesRepository.findAllById_TimestampBefore(id,end);
        }
        else{
            return resoucesRepository.findAllById_Timestamp(id,start, end);
        }
    }


    public List<String> getAllIds() {
        DistinctIterable<String> distinctIterable = mongoTemplate.getCollection("gateway").distinct("Id", String.class);
        LinkedList<String> list = new LinkedList<>();
        for (String id : distinctIterable) {
            list.add(id);
        }
        return list;
    }

    

}
