package com.example.sushiroo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    public static void main(String[] arg) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "asdadas";
        String encoded = bCryptPasswordEncoder.encode(rawPassword);
        System.out.println(encoded);
    }
}
