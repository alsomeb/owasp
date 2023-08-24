package com.test.owasp.obsolet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
// Todo Används ej i denna uppgift
@Controller
public class StaticController {

    @GetMapping("/user")
    public String showIndex(Model model) {
        return "index.html";
    }

}
