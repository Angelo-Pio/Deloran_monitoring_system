package com.tlc.deloran.controller;

import com.tlc.deloran.model.Resources;
import com.tlc.deloran.service.IntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {


    @Autowired
    private IntService intService;

    @GetMapping("/getByTimestamp")
    public List<Resources> getByTimestamp(
            @RequestParam(value = "start",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end){
        return intService.getAllResourcesByTimestamp(start, end);
    }

    @GetMapping("/getAll")
    public List<Resources> getAll(){
        return intService.getAllResources();
    }

    @GetMapping("/getAllById&Timestamp")
    public List<Resources> getAllByIdAndTimestamp(
            @RequestParam(value = "start",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(value = "id",required = false) String[] id
    ){
        return intService.getAllByIdAndTimestamp(id, start, end);
    }

    @GetMapping("/getAllIds")
    public List<String> getAllIds() {
        return intService.getAllIds();
    }


}
