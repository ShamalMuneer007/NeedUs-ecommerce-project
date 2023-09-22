package com.needus.ecommerce.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shop")
public class AnonymousController {
    @GetMapping("/home")
    public String landingPage(){
        return "shop/home";
    }
}