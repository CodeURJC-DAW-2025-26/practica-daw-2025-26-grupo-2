package es.dawgrupo2.zendashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login"; // The interceptor adds the "token" to the model automatically
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // The interceptor adds the "token" to the model automatically
    }

    @GetMapping("/loginerror")
    public String loginError(RedirectAttributes re) {
        re.addFlashAttribute("message", "Credenciales inválidas");
        re.addFlashAttribute("backLink", "/login");
        return "redirect:/customerror"; // The interceptor adds the "token" to the model automatically
    }
}