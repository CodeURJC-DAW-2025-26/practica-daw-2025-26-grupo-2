package es.dawgrupo2.zendashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {
    @GetMapping("/customerror")
    public String notFound(Model model) {
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", "Recurso no encontrado");
        }
        if (!model.containsAttribute("backLink")) {
            model.addAttribute("backLink", "/");
        }
        return "error";
    }
}
