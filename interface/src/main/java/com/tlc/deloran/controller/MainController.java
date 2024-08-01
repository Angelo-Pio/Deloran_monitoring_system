package com.tlc.deloran.controller;

import com.tlc.deloran.model.Resources;
import com.tlc.deloran.service.ResourcesService;
import com.tlc.deloran.service.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private PacketService packetService;

    @GetMapping("/")
    public String home(Model model){

        List<Resources> gateways = resourcesService.getAllResources();
        model.addAttribute("gateways", gateways);
        model.addAttribute("gateways_ids", resourcesService.getAllIds());

        return "index";


    }



    @GetMapping("/realtime")
    public String realtime(Model model){

        List<Resources> gateways = resourcesService.getAllResources();
        model.addAttribute("gateways", gateways);
        model.addAttribute("gateways_ids", resourcesService.getAllIds());
        return "realtime";

    }

    @GetMapping("/packets")
    public String packets(Model model){

        return "packets";

    }
}
