package com.example.sushiroo.controller;

import com.example.sushiroo.model.User;
import com.example.sushiroo.repository.UserRepository;
import com.example.sushiroo.service.UserDetailService;
import org.apache.commons.codec.language.bm.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailService userDetailService;

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

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/";
    }

    @GetMapping("/testing")
    public String test() {
        return "testing";
    }

    @GetMapping("/setting")
    public String setting(Model model) {
        model.addAttribute("user", userDetailService.getCurrentUser());
        model.addAttribute("error", "");
        return "setting";
    }

    @PostMapping("/setting/save")
    public String settingSave(@ModelAttribute("user") User user, @RequestParam("currentPassword") String curPasswordGuess, Model model) {
        int i = 0;
        User currentUser = userDetailService.getCurrentUser();
        String username = user.getUsername();
        String email = user.getEmail();
        String newPassword = user.getPassword();
        String curPasswordBCrypt = currentUser.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        /* Validate current password */
        if (!(bCryptPasswordEncoder.matches(curPasswordGuess, curPasswordBCrypt))) {
            model.addAttribute("user", currentUser);
            model.addAttribute("error", "Current password is wrong! Try again!");
            return "setting";
        }
        else {
            /* Validate username */
            if (!(username.equals(currentUser.getUsername()))) {
                if (userRepository.findByUsername(username).isPresent()) {
                    model.addAttribute("user", currentUser);
                    model.addAttribute("error", "Username has been used, try using another one!");
                    return "setting";
                } else {
                    if (username.length() != 0 & username.length() > 15) {
                        model.addAttribute("user", currentUser);
                        model.addAttribute("error", "Username has to be greater than 0 character " +
                                "and less than 20 characters!");
                        return "setting";
                    }
                    i += 1;
                    currentUser.setUsername(username);
                    System.out.println("validated username");
                }
            }
            /* Validate email */
            if (!(email.equals(currentUser.getEmail()))) {
                if (userRepository.findByEmail(email).isPresent()) {
                    model.addAttribute("user", currentUser);
                    model.addAttribute("error", "Email has been used, try using another one!");
                    return "setting";
                } else {
                    i += 1;
                    currentUser.setEmail(email);
                    System.out.println("validated email");
                }
            }
            /* Validate new password */
            if (newPassword.length() > 0) {
                String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(newPassword);
                if (!(matcher.matches())) {
                    model.addAttribute("user", currentUser);
                    model.addAttribute("error", "Invalid password!");
                    return "setting";
                }
                i += 1;
                currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
                System.out.println("validated password");
            }
            if (i != 0) {
                userRepository.save(currentUser);
                model.addAttribute("user", currentUser);
                model.addAttribute("error", "Success!");
                return "setting";
            }
        }
        //System.out.println(user.getPassword());
        return "redirect:/setting";
    }

}
