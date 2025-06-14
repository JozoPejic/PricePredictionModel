package com.jozo.pricepredictionmodel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/my-stocks")
    public String myStocksPage() {
        return "my-stocks";
    }

    @GetMapping("/my-followed-stocks")
    public String myFollowedStocksPage() {
        return "my-followed-stocks";
    }

    @GetMapping("/news-display")
    public String newsDisplayPage() {
        return "news-display";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }
    @GetMapping("/stock-chart")
    public String stockChartPage() {
        return "stock-chart";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }

}
