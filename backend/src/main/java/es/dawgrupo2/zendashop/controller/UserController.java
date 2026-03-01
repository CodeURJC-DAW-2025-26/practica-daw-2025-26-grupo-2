package es.dawgrupo2.zendashop.controller;

import java.io.IOException;
//import java.lang.foreign.Linker.Option;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;
import java.sql.Blob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PathVariable;

import es.dawgrupo2.zendashop.service.OrderService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServlet;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import es.dawgrupo2.zendashop.model.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tools.jackson.databind.ObjectMapper;

@Controller
public class UserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String showUsers(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("users", userService.findByDisabledFalse(pageable));
        model.addAttribute("hasMore", userService.findByDisabledFalse(pageable.next()).hasContent());
        return "all_users";
    }

    @GetMapping("/loadMoreUsers")
    public String loadMoreUsers(Model model, @PageableDefault(size = 10) Pageable pageable,
            HttpServletResponse response) {
        model.addAttribute("users", userService.findByDisabledFalse(pageable));
        response.addHeader("X-Has-More", String.valueOf(userService
                .findByDisabledFalse(pageable.next())
                .hasContent()));
        return "users_cards";
    }

    @GetMapping("/user/{id}")
    public String showUserID(Model model, @PathVariable Long id, HttpServletRequest request) {

        Optional<User> op = userService.findById(id);

        if (op.isPresent()) {
            if (!request.isUserInRole("ADMIN") && !op.get().getEmail().equals(request.getUserPrincipal().getName())) {
                model.addAttribute("message", "Quieto parao! No tienes permiso para ver este perfil");
                model.addAttribute("backLink", "/");
                return "customError";
            }
            User user = op.get();
            model.addAttribute("user", user);
            model.addAttribute("hasAvatar", user.getHasAvatar());
            model.addAttribute("monthlyLabelsJson", toJson(orderService.getMonthlyLabelsLastMonths(12)));
            model.addAttribute("monthlyMeanTicketJson",
                    toJson(orderService.getMonthlyMeanTicketLastMonthsById(12, user.getId())));
            model.addAttribute("yearlyLabelsJson", toJson(orderService.getYearlyLabelsLastYears(6)));
            model.addAttribute("yearlyMeanTicketJson",
                    toJson(orderService.getYearlyMeanTicketLastYearsById(6, user.getId())));
            return "user_profile";
        } else {
            model.addAttribute("message", "¿Qué buscabas? Usuario no encontrado");
            model.addAttribute("backLink", "/");
            return "customError";
        }
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        return "redirect:/user/" + user.getId();
    }

    @GetMapping("/user/{id}/edit")
    public String editUser(Model model, @PathVariable Long id, HttpServletRequest request) {

        Optional<User> op = userService.findById(id);

        if (op.isPresent()) {
            User userToEdit = op.get();
            String loggedInEmail = request.getUserPrincipal().getName();
            boolean isAdmin = request.isUserInRole("ADMIN");

            if (!isAdmin && !userToEdit.getEmail().equals(loggedInEmail)) {
                model.addAttribute("message", "Quieto parao! No tienes permiso para editar este perfil");
                model.addAttribute("backLink", "/");
                return "customError";
            }

            model.addAttribute("user", userToEdit);
            return "register";
        } else {
            model.addAttribute("message", "¿Qué buscabas? Usuario no encontrado");
            model.addAttribute("backLink", "/");
            return "customError";
        }
    }

    @PostMapping("/user/{id}/edit")
    public String editUserProcess(Model model, User editedUser, @PathVariable Long id, MultipartFile imageAvatar,
            HttpServletRequest request, @RequestParam(name = "updateImage", defaultValue = "false") boolean updateImage) {

        Optional<User> op = userService.findById(id);

        String newPassword = editedUser.getEncodedPassword();

        if (op.isPresent()) {
            User originalUser = op.get();
            String originalEmail = originalUser.getEmail();
            String loggedInEmail = request.getUserPrincipal().getName();
            boolean isAdmin = request.isUserInRole("ADMIN");

            if (!isAdmin && !originalUser.getEmail().equals(loggedInEmail)) {
                model.addAttribute("message", "Quieto parao! No tienes permiso para editar este perfil");
                model.addAttribute("backLink", "/");
                return "customError";
            }

            originalUser.setName(editedUser.getName());
            originalUser.setSurname(editedUser.getSurname());
            originalUser.setEmail(editedUser.getEmail());

            if (newPassword != null && !newPassword.isBlank()) {
                originalUser.setEncodedPassword(passwordEncoder.encode(editedUser.getEncodedPassword()));
            }

            if (updateImage) {
                if (imageAvatar != null && !imageAvatar.isEmpty()) {
                    try {
                        userService.save(originalUser, imageAvatar);
                    } catch (IOException e) {
                        throw new RuntimeException("Error al guardar la imagen", e);
                    }
                } else {
                    originalUser.setAvatar(null);
                    userService.save(originalUser);
                }
            }

            if (originalEmail.equals(loggedInEmail)) {
                Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                UserDetails updatedDetails = userDetailsService.loadUserByUsername(originalUser.getEmail());

                UsernamePasswordAuthenticationToken newAuth = UsernamePasswordAuthenticationToken.authenticated(
                        updatedDetails,
                        currentAuth.getCredentials(),
                        updatedDetails.getAuthorities());
                newAuth.setDetails(currentAuth.getDetails());

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(newAuth);
                SecurityContextHolder.setContext(context);
                request.getSession(true).setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        context);
            }

            return "redirect:/user/" + originalUser.getId();
        } else {
            model.addAttribute("message", "¿Qué buscabas? Usuario no encontrado");
            model.addAttribute("backLink", "/");
            return "customError";
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
    public String deleteUser(Model model, @PathVariable Long id, HttpServletRequest request) {
        User userSession = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
        if (!request.isUserInRole("ADMIN") && !userSession.getId().equals(id)) {
            model.addAttribute("message", "Quieto parao! No tienes permiso para eliminar este usuario");
            model.addAttribute("backLink", "/");
            return "customError";
        }
        Optional<User> op = userService.findById(id);
        if (op.isPresent()) {
            userService.disableUser(id);
            if (userSession.getId().equals(id)) {
                try {
                    request.logout();
                } catch (Exception e) {
                    throw new RuntimeException("Error al cerrar sesión después de eliminar el usuario", e);
                }
            }
            return "redirect:/";
        } else {
            model.addAttribute("message", "¿Qué buscabas? Usuario no encontrado");
            model.addAttribute("backLink", "/");
            return "customError";
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "[]";
        }
    }
}
