package com.tlc.deloran.controller;

import com.tlc.deloran.model.Resources;
import com.tlc.deloran.service.IntService;
import com.tlc.deloran.service.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IntCotroller {

    @Autowired
    private IntService intService;

    @Autowired
    private PacketService packetService;

    @GetMapping("/")
    public String home(Model model){

        List<Resources> gateways = intService.getAllResources();
        model.addAttribute("gateways", gateways);
        model.addAttribute("gateways_ids", intService.getAllIds());

        return "index";


    }



    @GetMapping("/realtime")
    public String realtime(Model model){

        List<Resources> gateways = intService.getAllResources();
        model.addAttribute("gateways", gateways);
        model.addAttribute("gateways_ids", intService.getAllIds());
        return "realtime";

    }

    @GetMapping("/packets")
    public String packets(Model model){

        //Restituisci un'insieme di oggetti Packet (timestamp + json)
        //Calcola quanti packets sono arrivati in un intervallo di 5 secondi e restituisci il numero -> front end ?

        // TO SOLVE
        /*
        * Mock up dei pacchetti nel ResourceExtractor -> nuova coda in rabbitmq DONE
        * Salvataggio dei pacchetti nel db DONE
        * Extrazione del Packet e passaggio a front end DONE
        * Generazione del grafico lato front end, calcolare Dataset lato backend? -> like:
        * now: 12:50:55 -> last five minutes 12:45:55
        *
        * */

        return "packets";

    }
}
