package com.example.sushiroo.controller;

import com.example.sushiroo.model.OWLEntity;
import com.example.sushiroo.model.Order;
import com.example.sushiroo.model.User;
import com.example.sushiroo.model.UserDetail;
import com.example.sushiroo.service.OWLService;
import com.example.sushiroo.service.OrderService;
import com.example.sushiroo.service.UserDetailService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class OWLController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OWLService owlService;

    @Autowired
    private UserDetailService userDetailService;

    @GetMapping("/homepage")
    public String getHomePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (userDetailService.getCurrentUser() == null) {
                UserDetail userDetail = (UserDetail) authentication.getPrincipal();
                Long userId = userDetail.getId();
                String username = userDetail.getName();
                String email = authentication.getName();
                String password = userDetail.getPassword();
                userDetailService.setCurrentUser(userId, username, email, password);
            }
        }
        model.addAttribute("currentUser", userDetailService.getCurrentUser());
        model.addAttribute("allSushiFromType", owlService.getAllSushi());
        owlService.setCurrentFilterList(new ArrayList<>());
        model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
        return "homepage";
    }

    @GetMapping("/homepage/sushi/{variable}")
    public String getAllSushi(@PathVariable String variable, Model model) {
        System.out.println("hi");
        model.addAttribute("currentUser", userDetailService.getCurrentUser());
        model.addAttribute("allSushiFromType", owlService.getAllSushiFromType(variable));
        model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
        return "homepage";
    }

    @GetMapping("/homepage/sushi/search")
    public String getSearchedSushi(@RequestParam(value = "query", required = true) String searchValue, Model model){
        //System.out.println(searchValue);
        model.addAttribute("currentUser", userDetailService.getCurrentUser());
        model.addAttribute("allSushiFromType", owlService.searchSushiFromName(searchValue));
        model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
        return "homepage";
    }

    @GetMapping("/homepage/filterSushi")
    public String allergenFilter(@RequestParam(value = "allergens", required = false) String[] allergens, Model model){
        model.addAttribute("currentUser", userDetailService.getCurrentUser());
        if (allergens == null) {
            owlService.setCurrentFilterList(new ArrayList<>());
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiList());
            model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
            //System.out.println("empty");
        }
        else {
            model.addAttribute("allSushiFromType", owlService.sushiFilter(allergens));
            model.addAttribute("selectedAllergens", owlService.getCurrentFilterList());
        }
        return "homepage";
    }

    /* Change order of a sushi in homepage */
    @PostMapping("/homepage/order/{variable}")
    public String changeOrder(@RequestParam(value = "target") int s, @PathVariable String variable, Model model){
        if (variable.equals("decrementOrder")) {
            owlService.changeOrderNumber(false, s);
        } else if (variable.equals("incrementOrder")){
            owlService.changeOrderNumber(true, s);
        }
        List<String> currentFilterList = owlService.getCurrentFilterList();
        model.addAttribute("currentUser", userDetailService.getCurrentUser());
        model.addAttribute("selectedAllergens", currentFilterList);
        if (currentFilterList.isEmpty()) {
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiList());
        } else {
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiListAfterFilter());
        }

        return "homepage";

    }

    /* Change order of a sushi in currentOrder page */
    @PostMapping("/myOrder/{variable}")
    public String decrementOrderInMyOrder(@RequestParam(value = "target") int s,  @PathVariable String variable, Model model){
        //System.out.println(s);
        if (variable.equals("decrementOrder")) {
            owlService.changeOrderNumber(false, s);
        } else if (variable.equals("incrementOrder")){
            owlService.changeOrderNumber(true, s);
        }
        orderService.setDateTimeToCurrent();
        orderService.setFutureDateTime(0, 1);
        model.addAttribute("minimumDateTime", orderService.getLocalDateTime());
        orderService.setFutureDateTime(10, 0);
        model.addAttribute("maximumDateTime", orderService.getLocalDateTime());

        model.addAttribute("currentOrder", owlService.getCurrentOrder());
        return "currentOrder";
    }

    @GetMapping("/myOrder")
    public String viewMyOrder(Model model) {
        orderService.setDateTimeToCurrent();
        orderService.setFutureDateTime(0, 1);
        model.addAttribute("minimumDateTime", orderService.getLocalDateTime());
        orderService.setFutureDateTime(10, 0);
        model.addAttribute("maximumDateTime", orderService.getLocalDateTime());

        model.addAttribute("currentOrder", owlService.getCurrentOrder());
        return "currentOrder";
    }

    /* Sushi Detail */
    @GetMapping("/homepage/sushi/detail/{variable}")
    public String getSushiDetail(@PathVariable int variable, Model model) {
        //OWLEntity e = owlService.getSushiDetail(variable);
        model.addAttribute("sushiDetail", owlService.getSushiDetail(variable));
        return "detail";
    }

    @GetMapping("/homepage/return")
    public String returnHomepage(Model model) {
        model.addAttribute("currentUser", userDetailService.getCurrentUser());
        List<String> currentFilterList = owlService.getCurrentFilterList();
        if (!(currentFilterList.isEmpty())) {
            //System.out.println("1");
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiListAfterFilter());
            model.addAttribute("selectedAllergens", currentFilterList);
        } else {
            //System.out.println("2");
            model.addAttribute("allSushiFromType", owlService.getCurrentSushiList());
            model.addAttribute("selectedAllergens", currentFilterList);
        }
        return "homepage";
    }

    @PostMapping("/order/submit")
    public String submitOrder(@RequestParam(value = "orderTime") String date) {
        List<OWLEntity> currentOrder = owlService.getCurrentOrder();

        if (currentOrder.isEmpty()) {
            return "redirect:/myOrder";
        }

        User user = userDetailService.getCurrentUser();
        Order order = new Order();
        String content = "";
        for (OWLEntity e: currentOrder) {
            content = content + e.getSushiName() + " x " + e.getOrder() + " ";
        }
        order.setUser(user);
        order.setContent(content);
        order.setDate(LocalDateTime.parse(date, orderService.getFormatter()));
        orderService.saveOrder(order);
        owlService.resetAllSushiOrder();
        return "redirect:/homepage";
    }

    @PostMapping("/incrementValues")
    public Integer incrementHi(@RequestParam("index") Integer index) {
        System.out.println("hiiiiiiiiiiiasdasdas");
        return owlService.changeListByIndex(index);
    }



}
