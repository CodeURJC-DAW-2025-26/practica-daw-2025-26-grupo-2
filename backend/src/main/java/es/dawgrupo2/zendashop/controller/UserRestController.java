package es.dawgrupo2.zendashop.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.dawgrupo2.zendashop.service.UserService;
import es.dawgrupo2.zendashop.basicDTO.UserBasicMapper;
import es.dawgrupo2.zendashop.extendedDTO.UserExtendedMapper;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import es.dawgrupo2.zendashop.basicDTO.UserBasicDTO;
import es.dawgrupo2.zendashop.extendedDTO.UserExtendedDTO;
import jakarta.servlet.http.HttpServletRequest;
import es.dawgrupo2.zendashop.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.access.AccessDeniedException;



@RestController
@RequestMapping("/api/v1")
public class UserRestController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserBasicMapper userBasicMapper;

    @Autowired
    private UserExtendedMapper userExtendedMapper;

    // Endpoint to obtain a paginated list of users, excluding disabled ones
    @GetMapping("/users")
    public Page<UserBasicDTO> getUsers(@PageableDefault(size = 20) Pageable pageable) {
        return userService.findByDisabledFalse(pageable).map(userBasicMapper::toDTO);
    }

    // Endpoint to obtain the current user
    @GetMapping("/me")
    public UserExtendedDTO getCurrentUser(HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new RuntimeException("User not found"));
        return userExtendedMapper.toDTO(user);
    }

    @PostMapping("/me")
    public String postMethodName(@RequestBody String entity) {
        //implement logic, right now just returns the entity received in the request body
        return entity;
    }
    
    // Endpoint to delete a user, only if the requester is an admin or the user itself
    @DeleteMapping("/users/{id}")
        public UserExtendedDTO deleteUser(@PathVariable long id, HttpServletRequest request) {
            User user = userService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User not found"));

            if (!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
                throw new AccessDeniedException("You are not authorized to delete this user");
            }

            return null;
        }

    
}
