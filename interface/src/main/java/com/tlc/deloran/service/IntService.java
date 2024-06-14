package com.tlc.deloran.service;

import com.mongodb.client.DistinctIterable;
import com.tlc.deloran.model.Gateway;
import com.tlc.deloran.repository.IntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

@Service
public class IntService {

    @Autowired
    private IntRepository intRepository;

    @Autowired
    private MongoTemplate mongoTemplate;



    public List<Gateway> getAllGateways(){
        return intRepository.findAll();
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


        /*List<Gateway> allByTimestamp = intRepository.findAllByTimestamp(start, end);
        return allByTimestamp;*/
    }

    public List<Gateway> getAllByIdAndTimestamp(String[] id, LocalDateTime start, LocalDateTime end){

        start = toUTC(start);
        end = toUTC(end);
        if(id == null || id.length == 0){
            return getAllGatewaysByTimestamp(start, end);
        }else{
            return getAllGatewaysById(id,start,end);
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
