package com.myproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RedirectController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @GetMapping("/home")
    public String showOptionPage() {
        return "home";
    }

    @GetMapping("/register-success")
    public String registerSuccess(){
        return "register-success";
    }

    @GetMapping("/chat-room")
    public String chatRoom(@RequestParam("room-id") Integer roomId){
        return "chat-room";
    }
}
