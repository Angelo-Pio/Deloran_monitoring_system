package com.tlc.deloran.controller;

import com.tlc.deloran.model.Gateway;
import com.tlc.deloran.repository.IntRepository;
import com.tlc.deloran.service.IntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return "index";


    }

    @GetMapping("/graphs")
    public String graphs(Model model){

        List<Gateway> gateways = intService.getAllGateways();
        model.addAttribute("gateways", gateways);
        return "graphs";

    }
}
