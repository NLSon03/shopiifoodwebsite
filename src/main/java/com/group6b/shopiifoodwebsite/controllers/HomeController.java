package com.group6b.shopiifoodwebsite.controllers;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home() {
        return "home/index";
    }
}
