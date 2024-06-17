package com.tlc.deloran.controller;

import com.tlc.deloran.model.Gateway;
import com.tlc.deloran.repository.IntRepository;
import com.tlc.deloran.service.IntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Controller
public class IntCotroller {

    @Autowired
    private IntService intService;

    @GetMapping("/")
    public String home(Model model){

        List<Gateway> gateways = intService.getAllGateways();
        model.addAttribute("gateways", gateways);
        model.addAttribute("gateways_ids", intService.getAllIds());

        return "index";


    }



    @GetMapping("/realtime")
    public String realtime(Model model){

        List<Gateway> gateways = intService.getAllGateways();
        model.addAttribute("gateways", gateways);
        return "realtime";

    }
}
