package com.library.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String index() {
        return "index"; // intro page with modal
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    //@GetMapping("/admin")
    //public String admin() {
        //return "admin";
    //}

    @GetMapping("/staff")
    public String staff() {
        return "staff";
    }
}