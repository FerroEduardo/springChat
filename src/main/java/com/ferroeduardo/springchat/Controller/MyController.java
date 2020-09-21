package com.ferroeduardo.springchat.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

    @GetMapping("/")
    public String getIndexRedirect() {
        return "redirect:/chat";
    }
}
