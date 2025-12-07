package com.example.springai.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class SpringWebController {

    @GetMapping("/")
    String redirectToDefault() {
        return "redirect:/swagger-ui/index.html";
    }
}