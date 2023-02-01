package com.example.sushiroo.controller;

import com.example.sushiroo.OWLService;
import com.example.sushiroo.User;
import com.example.sushiroo.UserRepository;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Controller
public class OWLController {

    @Autowired
    private OWLService owlService;

    @GetMapping("/sushi/{variable}")
    public String getAllSushi(@PathVariable String variable, Model model) {
        model.addAttribute("allSushiFromType", owlService.getSushiFromType(variable));
        return "homepage";
    }

    @GetMapping("/sushi/search")
    public String getSearchedSushi(@RequestParam(value = "query", required = true) String searchValue, Model model){
        //System.out.println(searchValue);
        model.addAttribute("allSushiFromType", owlService.getSushiFromName(searchValue));
        return "homepage";
    }

}
