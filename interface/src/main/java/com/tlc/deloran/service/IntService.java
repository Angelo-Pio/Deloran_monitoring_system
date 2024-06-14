package com.tlc.deloran.service;

import com.tlc.deloran.model.Gateway;
import com.tlc.deloran.repository.IntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class IntService {

    @Autowired
    private IntRepository intRepository;

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
        if(id == null || id.length == 0){
            return getAllGatewaysByTimestamp(start, end);
        }else{
            return getAllGatewaysById(id,start,end);
        }
    }

    private List<Gateway> getAllGatewaysById(String[] id, LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
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


}
