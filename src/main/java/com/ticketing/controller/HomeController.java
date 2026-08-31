package com.ticketing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Kök dizine (http://localhost:8080/) gelen istekleri doğrudan
     * static/index.html görsel paneline yönlendirir.
     */
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}
