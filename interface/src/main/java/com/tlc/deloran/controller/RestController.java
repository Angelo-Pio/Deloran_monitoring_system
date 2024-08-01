package com.tlc.deloran.controller;

import com.tlc.deloran.model.Packet;
import com.tlc.deloran.model.Resources;
import com.tlc.deloran.service.ResourcesService;
import com.tlc.deloran.service.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {


    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private PacketService packetService;

    @GetMapping("/resources/getByTimestamp")
    public List<Resources> getByTimestamp(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return resourcesService.getAllResourcesByTimestamp(start, end);
    }

    @GetMapping("/resources/getAll")
    public List<Resources> getAll() {
        return resourcesService.getAllResources();
    }

    @GetMapping("/resources/getAllById&Timestamp")
    public List<Resources> getAllByIdAndTimestamp(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(value = "id", required = false) String[] id
    ) {
        return resourcesService.getAllByIdAndTimestamp(id, start, end);
    }

    @GetMapping("/resources/getAllIds")
    public List<String> getAllIds() {
        return resourcesService.getAllIds();
    }

    @GetMapping("/packets/getAllLastFiveMinutesPackets")
    public List<Packet> getAllLastFiveMinutesPackets() {
        return packetService.getAllLastFiveMinutesPackets();
    }

    @GetMapping("/packets/getAll")
    public List<Packet> getAllPackets() {
        return packetService.getAllPackets();
    }


}
