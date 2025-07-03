package com.chatapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/login")
    public String serveLogin() {
        // returns login.html from static/ instead of template/ by thymleaf or JSP
        return "redirect:/login.html";
    }

    @GetMapping("/chat")
    public String serveChat() {
        // returns chat.html from static/ instead of template/ by thymleaf or JSP
        return "redirect:/chat.html";
    }

    @GetMapping("/dashboard")
    public String serveDashboard() {
        // returns chat.html from static/ instead of template/ by thymleaf or JSP
        return "redirect:/dashboard.html";
    }
}
