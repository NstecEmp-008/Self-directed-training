package com.training.ec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class errorController {

    @GetMapping("/error")
    public String accessDenied() {
        // templates/error/access-denied.html を返す
        return "error";
    }
}
