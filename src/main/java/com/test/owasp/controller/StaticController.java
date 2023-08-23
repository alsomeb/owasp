package com.test.owasp.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @GetMapping("/user")
    public String showIndex(Model model) {
        return "index.html";
    }

}
