package com.tlc.deloran.service;

import com.tlc.deloran.model.Packet;
import com.tlc.deloran.repository.PacketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class PacketService {
    
    @Autowired
    private PacketsRepository repo;
    
    
    public List<Packet> getAllLastFiveMinutesPackets(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last5Minutes = now.minusMinutes(5);
        return repo.findAllByTimestamp(last5Minutes);

    }

    public List<Packet> getAllPackets() {

        return  repo.findAll();
    }
}
