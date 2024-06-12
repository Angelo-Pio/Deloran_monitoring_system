package com.tlc.deloran.service;

import com.tlc.deloran.model.Gateway;
import com.tlc.deloran.repository.IntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class IntService {

    @Autowired
    private IntRepository intRepository;

    public List<Gateway> getAllGateways(){
        return intRepository.findAll();
    }

}
