package es.dawgrupo2.zendashop.controller;

//import java.lang.foreign.Linker.Option;
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

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

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

    @Autowired
	private PasswordEncoder passwordEncoder;

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
            User user = op.get();
            model.addAttribute("user", user);
            model.addAttribute("hasAvatar", user.getAvatar()!=null);
            return "user_profile";
        } else {
            model.addAttribute("element", "Usuario");
            model.addAttribute("masculine", true);
            return "not_found";
        }
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Optional<User> op = userService.findByEmail(principal.getName());
        
        if (op.isPresent()){
            User user = op.get();
            model.addAttribute("user", user);
            model.addAttribute("hasAvatar", user.getAvatar()!=null);
        } else {
            return "redirect:/login";
        }
        return "user_profile";
    }

    @GetMapping("/users")
	public String showUsers(Model model) {
		model.addAttribute("users", userService.findAll());
		return "all_users";
	}

    @GetMapping("/user/{id}/edit")
    public String editUser(Model model, @PathVariable Long id, HttpServletRequest request) {

        Optional<User> op = userService.findById(id);

        if (op.isPresent()) {
            User userToEdit = op.get();
            String loggedInEmail = request.getUserPrincipal().getName();
            boolean isAdmin = request.isUserInRole("ADMIN");

            if (!isAdmin && !userToEdit.getEmail().equals(loggedInEmail)) {
                return "redirect:/"; 
            }

            model.addAttribute("user", userToEdit);
            return "register";
        } else {
            model.addAttribute("element", "Usuario");
            model.addAttribute("masculine", true);
            return "not_found";
        }
    }

    @PostMapping("/user/{id}/edit")
    public String editUserProcess(Model model, User editedUser, @PathVariable Long id, MultipartFile imageAvatar, HttpServletRequest request) {

        Optional<User> op = userService.findById(id);

        String newPassword = editedUser.getEncodedPassword();

        if (op.isPresent()) {
            User originalUser = op.get();
            String loggedInEmail = request.getUserPrincipal().getName();
            boolean isAdmin = request.isUserInRole("ADMIN");

            if (!isAdmin && !originalUser.getEmail().equals(loggedInEmail)) {
                return "redirect:/";
            }

            originalUser.setName(editedUser.getName());
            originalUser.setSurname(editedUser.getSurname());
            originalUser.setEmail(editedUser.getEmail());
    
            if (newPassword != null && !newPassword.isBlank()) {
                originalUser.setEncodedPassword(passwordEncoder.encode(editedUser.getEncodedPassword()));
            }

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
            Resource AvatarFile = new InputStreamResource(AvatarImage.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(AvatarFile)
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
