package com.example.sushiroo.controller;

import com.example.sushiroo.OWLEntity;
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
import java.util.*;

@Controller
public class OWLController {

    @Autowired
    private OWLService owlService;

    @GetMapping("/homepage")
    public String getHomePage(Model model) {
        model.addAttribute("allSushiFromType", owlService.getAllSushi());
        model.addAttribute("selectedAllergens", owlService.resetCurrentFilterList());
        return "homepage";
    }

    @GetMapping("/homepage/sushi/{variable}")
    public String getAllSushi(@PathVariable String variable, Model model) {
        model.addAttribute("allSushiFromType", owlService.getAllSushiFromType(variable));
        model.addAttribute("selectedAllergens", owlService.resetCurrentFilterList());
        return "homepage";
    }

    @GetMapping("/homepage/sushi/search")
    public String getSearchedSushi(@RequestParam(value = "query", required = true) String searchValue, Model model){
        //System.out.println(searchValue);
        model.addAttribute("allSushiFromType", owlService.searchSushiFromName(searchValue));
        model.addAttribute("selectedAllergens", owlService.resetCurrentFilterList());
        return "homepage";
    }

    @GetMapping("/homepage/filterSushi")
    public String allergenFilter(@RequestParam(value = "allergens", required = false) String[] allergens, Model model){
        //System.out.println(Arrays.asList(allergens));
        //List<String> allergensList = Arrays.asList(allergens);
        if (allergens == null) {
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiList());
            model.addAttribute("selectedAllergens", owlService.resetCurrentFilterList());
            //System.out.println("empty");
        }
        else {
            //System.out.println(Arrays.asList(allergens));
            model.addAttribute("allSushiFromType", owlService.sushiFilter(allergens));
            model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
            //System.out.println(Arrays.asList(allergens));
        }
        return "homepage";
    }

    @PostMapping("/homepage/decrementOrder")
    public String decrementOrder(@RequestParam(value = "target") String s, Model model){
        //System.out.println(s);
        owlService.changeOrderNumber(false, s);
        model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
        if (owlService.getCurrentSushiListAfterFilter().isEmpty()) {
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiList());
        } else {
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiListAfterFilter());
        }
        return "homepage";
    }

    @PostMapping("/homepage/incrementOrder")
    public String incrementOrder(@RequestParam(value = "target") String s, Model model){
        //System.out.println(s);
        owlService.changeOrderNumber(true, s);
        model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
        if (owlService.getCurrentSushiListAfterFilter().isEmpty()) {
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiList());
        } else {
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiListAfterFilter());
        }
        return "homepage";
    }


}
