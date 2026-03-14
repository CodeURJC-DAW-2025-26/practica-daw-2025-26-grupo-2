package es.dawgrupo2.zendashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login"; // The interceptor adds the "token" to the model automatically
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("isNew", true);
        return "register"; // The interceptor adds the "token" to the model automatically
    }

    @PostMapping("/register")
    public String addUser(Model model, User user, MultipartFile imageAvatar,
            @RequestParam(required = false) String rol, HttpServletRequest request) {
        String errorMsg = userService.validateFields(user, true);
        if (!errorMsg.isEmpty()) {
            model.addAttribute("message", errorMsg);
            model.addAttribute("backLink", "/register");
            return "customError";
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        userService.setUserRoles(user, rol);
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("message", "El email seleccionado ya pertence a un usuario registrado");
            model.addAttribute("backLink", "/register");
            return "customError";
        } else if (imageAvatar != null && !imageAvatar.isEmpty()) {
            try {
                userService.save(user, imageAvatar);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        } else {
            userService.save(user);
        }
        if (request.getUserPrincipal() != null) {
            return "redirect:/users";
        }

        return "redirect:/login";
    }

    @GetMapping("/loginerror")
    public String loginError(Model model) {
        model.addAttribute("message", "Usuario o contraseña incorrectos.");
        model.addAttribute("backLink", "/login");
        return "customError";
    }
}