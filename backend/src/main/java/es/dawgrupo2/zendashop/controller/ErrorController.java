package es.dawgrupo2.zendashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/loginerror")
    public String loginError(Model model) {
        model.addAttribute("message", "Usuario o contraseña incorrectos.");
        model.addAttribute("backLink", "/login");
        return "error";
    }

    @GetMapping("/accessDenied")
    public String accessDenied(Model model) {
        model.addAttribute("message", "No tienes permiso para acceder a esta página.");
        model.addAttribute("backLink", "/");
        return "error";
    }
}
