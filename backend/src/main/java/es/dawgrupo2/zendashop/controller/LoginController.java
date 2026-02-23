package es.dawgrupo2.zendashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login"; // El interceptor añade el "token" al modelo automáticamente
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // El interceptor añade el "token" al modelo automáticamente
    }
}