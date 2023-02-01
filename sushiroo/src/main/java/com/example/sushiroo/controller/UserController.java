package com.example.sushiroo.controller;

import com.example.sushiroo.OWLService;
import com.example.sushiroo.User;
import com.example.sushiroo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String getHomePage() {
        return "homepage";
    }

}
