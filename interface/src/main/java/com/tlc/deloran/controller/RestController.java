package com.tlc.deloran.controller;

import com.tlc.deloran.model.Gateway;
import com.tlc.deloran.service.IntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {


    @Autowired
    private IntService intService;

    @GetMapping("/getByTimestamp")
    public List<Gateway> getByTimestamp(
            @RequestParam(value = "start",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end){
        return intService.getAllGatewaysByTimestamp(start, end);
    }

    @GetMapping("/getAll")
    public List<Gateway> getAll(){
        return intService.getAllGateways();
    }

    @GetMapping("/getAllById&Timestamp")
    public List<Gateway> getAllByIdAndTimestamp(
            @RequestParam(value = "start",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(value = "id",required = false) String[] id
    ){
        return intService.getAllByIdAndTimestamp(id, start, end);
    }
}
