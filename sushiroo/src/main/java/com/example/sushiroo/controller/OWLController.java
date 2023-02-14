package com.example.sushiroo.controller;

import com.example.sushiroo.OWLEntity;
import com.example.sushiroo.OWLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    /* Sushi Detail */
    @GetMapping("/homepage/sushi/detail/{variable}")
    public String getSushiDetail(@PathVariable String variable, Model model) {
        //System.out.println("hiiii!!!!");
        OWLEntity e = owlService.getSushiDetail(variable);
        //System.out.println(e.getIngredients());
        model.addAttribute("sushiDetail", owlService.getSushiDetail(variable));
        return "detail";
    }

    @GetMapping("/homepage/return")
    public String returnHomepage(Model model) {
        if (owlService.getCurrentFilterList() != null & !(owlService.getCurrentFilterList().isEmpty())) {
            //System.out.println("1");
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiListAfterFilter());
            model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
        } else {
            //System.out.println("2");
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiList());
            model.addAttribute("selectedAllergens", owlService.resetCurrentFilterList());
        }
        return "homepage";
    }


}
