package com.tlc.deloran.service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoClient;
import com.tlc.deloran.model.Gateway;
import com.tlc.deloran.repository.IntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class IntService {

    @Autowired
    private IntRepository intRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoClient mongo;

    private static final String collectionName = "gateway";


    public List<Gateway> getAllGateways(){
        return intRepository.findAll();
    }

    // Main method
    public List<Gateway> getAllByIdAndTimestamp(String[] id, LocalDateTime start, LocalDateTime end){


        start = start != null ? toUTC(start) : null;
        end = end != null ? toUTC(end) : null;
        List<Gateway> gateways = new LinkedList<>();
        if(id == null || id.length == 0){
            gateways = getAllGatewaysByTimestamp(start, end);
        }else{
            gateways =  getAllGatewaysById(id,start,end);
        }
        return gateways;
    }

    public List<Gateway> getAllGatewaysByTimestamp(LocalDateTime start, LocalDateTime end){

//        If both null give last hour info
//        if end null give all from start to now
//        if start null give all until end

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHour = now.minusHours(1);
        if(start == null && end == null){
            return intRepository.findAllByTimestamp(lastHour, now);
        } else if (start != null && end == null) {
            return intRepository.findAllByTimestamp(start, now);
        }else if(start == null && end != null){
            return intRepository.findAllByTimestampBefore(end);
        }
        else{
            return intRepository.findAllByTimestamp(start, end);
        }
    }




    private List<Gateway> getAllGatewaysById(String[] id, LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = toUTC(LocalDateTime.now());
        LocalDateTime lastHour = now.minusHours(1);
        if(start == null && end == null){
            return intRepository.findAllById_Timestamp(id,lastHour, now);
        } else if (start != null && end == null) {
            return intRepository.findAllById_Timestamp(id,start, now);
        }else if(start == null && end != null){
            return intRepository.findAllById_TimestampBefore(id,end);
        }
        else{
            return intRepository.findAllById_Timestamp(id,start, end);
        }
    }


    //TODO collection name not hardcoded
    public List<String> getAllIds() {
        DistinctIterable<String> distinctIterable = mongoTemplate.getCollection("gateway").distinct("Id", String.class);
        LinkedList<String> list = new LinkedList<>();
        for (String id : distinctIterable) {
            list.add(id);
        }
        return list;
    }

    public LocalDateTime toUTC(LocalDateTime date){
        return date.atZone(ZoneId.of("Europe/Rome")).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }
}
