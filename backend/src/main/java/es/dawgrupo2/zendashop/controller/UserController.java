package es.dawgrupo2.zendashop.controller;

import java.lang.foreign.Linker.Option;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;
import java.sql.Blob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PathVariable;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServlet;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.core.io.InputStreamResource;

import es.dawgrupo2.zendashop.model.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        if (request.getUserPrincipal() != null) {
            model.addAttribute("logged", true);
            model.addAttribute("username", request.getUserPrincipal().getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/user/{id}")
    public String showUserID(Model model, @PathVariable Long id) {

        Optional<User> op = userService.findById(id);

        if (op.isPresent()) {
            model.addAttribute("user", op.get());
            return "user_profile";
        } else {
            model.addAttribute("element", "Usuario");
            model.addAttribute("masculine", true);
            return "not_found";
        }
    }

    @GetMapping("/user/profile")
    public String showUserProfile(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Optional<User> op = userService.findByEmail(principal.getName());
        model.addAttribute("user", op.get());
        return "user_profile";
        
    }

    @GetMapping("/user/{id}/edit")
    public String editUser(Model model, @PathVariable Long id) {

        Optional<User> op = userService.findById(id);

        if (op.isPresent()) {
            User user = op.get();
            model.addAttribute("user", user);
            return "register";
        } else {
            model.addAttribute("element", "Usuario");
            model.addAttribute("masculine", true);
            return "not_found";
        }
    }

    @PostMapping("/user/{id}/edit")
    public String editUserProcess(Model model, User editedUser, @PathVariable Long id, MultipartFile imageAvatar) {

        Optional<User> op = userService.findById(id);

        if (op.isPresent()) {

            User originalUser = op.get();

            originalUser.setName(editedUser.getName());
            originalUser.setSurname(editedUser.getSurname());
            originalUser.setEmail(editedUser.getEmail());
            originalUser.setId(id);

            try {
                userService.save(originalUser, imageAvatar);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save user with avatar", e);
            }
            return "redirect:/user/" + originalUser.getId();
        } else {
            model.addAttribute("element", "Usuario");
            model.addAttribute("masculine", true);
            return "not_found";
        }

    }

    @GetMapping("user/{id}/avatar")
    public ResponseEntity<Object> downloadAvatar(@PathVariable Long id) throws SQLException {

        Optional<User> op = userService.findById(id);

        if (op.isPresent() && op.get().getAvatar() != null) {
            Blob AvatarImage = op.get().getAvatar();
            InputStreamResource AvatarFile = new InputStreamResource(AvatarImage.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType("avatar.jpg")
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(AvatarFile);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("user/{id}/delete")
    public String deleteUser(Model model, @PathVariable Long id) {
        
        Optional<User> op = userService.findById(id);

        if (op.isPresent()) {
            User user = op.get();
            userService.save(user);
            return "redirect:/";
        } else {
            model.addAttribute("element", "Usuario");
            model.addAttribute("masculine", true);
            return "not_found";
        }
    }

}
