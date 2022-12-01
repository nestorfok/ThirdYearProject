package com.example.sushiroo;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OWLService owlService;

    @GetMapping("")
    public String viewHomePage(){
        return "index";
    }

    @GetMapping("/register")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user, Model model) {
        String response = "";
        Optional<User> existUser = userRepository.findByEmail(user.getEmail());
        if(existUser.isPresent()){
            response = "Error: User exist";
        }
        else {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
            response = "Success!";
            userRepository.save(user);
        }

        model.addAttribute("response", response);
        return "signup_form";
    }

    @GetMapping("/homepage")
    public String getHomePage(Model model) throws OWLOntologyCreationException {
        return "homepage";
    }

    @GetMapping("/getAllSushi")
    public String getAllSushi(Model model) {
        model.addAttribute("allSushi", owlService.getSushi());
        return "homepage";
    }

    @GetMapping("/getHandRoll")
    public String getHandRoll(Model model) {
        model.addAttribute("allSushi", owlService.getSushi());
        return "homepage";
    }
    @GetMapping("/getRoll")
    public String getRoll(Model model) {
        model.addAttribute("allSushi", owlService.getSushi());
        return "homepage";
    }

    @GetMapping("/getGunkan")
    public String getGunkan(Model model) {
        model.addAttribute("allSushi", owlService.getSushi());
        return "homepage";
    }

    @GetMapping("/getSeared")
    public String getSeared(Model model) {
        model.addAttribute("allSushi", owlService.getSushi());
        return "homepage";
    }
}
