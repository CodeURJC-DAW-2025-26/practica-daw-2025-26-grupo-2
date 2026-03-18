package es.dawgrupo2.zendashop.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.mustache.autoconfigure.MustacheProperties.Servlet;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;


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

    @GetMapping("/users/{id}")
    public UserExtendedDTO getUserById(@PathVariable long id, HttpServletRequest request) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));

        if(!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("You are not authorized to view this user");
        }

        return userExtendedMapper.toDTO(user);
    }

    

    @PostMapping("/users/register")
    public ResponseEntity<UserExtendedDTO> registerUser(@RequestBody UserExtendedDTO userDTO){

        User user = userExtendedMapper.toDomain(userDTO);

        // validate the user fields, including password
        String errMsg = userService.validateFields(user, true);
        if (!errMsg.isEmpty()) {
            throw new IllegalArgumentException(errMsg);
        }  

        // validate that the email is not already taken by another user
        if (userService.findByEmail(user.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email is already taken");
        }

        userService.setEncodedPassword(user);
        userService.setUserRoles(user, "USER");
        userService.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(location).body(userExtendedMapper.toDTO(user));

    }
    
    // Endpoint to obtain the current user
    @GetMapping("/me")
    public UserExtendedDTO getCurrentUser(HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("User not found"));
        return userExtendedMapper.toDTO(user);
    }

    @PutMapping("/me")
    public UserExtendedDTO updateCurrentUser(@RequestBody UserExtendedDTO updateUserDTO, HttpServletRequest request) {
        
        User originalUser = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("User not found"));
        
        User editedUser = userExtendedMapper.toDomain(updateUserDTO);

        // check if the request comes with a password, if not, keep the original one
        boolean updatePassword = editedUser.getEncodedPassword() != null && !editedUser.getEncodedPassword().isEmpty();

        String errMsg = userService.validateFields(editedUser, updatePassword);
        if (!errMsg.isEmpty()) {
            throw new IllegalArgumentException(errMsg);
        }

        // validate that the email is not already taken by another user
        if (!originalUser.getEmail().equals(editedUser.getEmail()) && userService.findByEmail(editedUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already taken");
        }

        userService.updateUser(originalUser, editedUser, updatePassword, false);
        userService.save(originalUser);

        return userExtendedMapper.toDTO(originalUser);
    }
    

    // Endpoint to delete a user, only if the requester is an admin or the user itself
    @DeleteMapping("/users/{id}")
        public UserExtendedDTO deleteUser(@PathVariable long id, HttpServletRequest request) {
            User user = userService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User not found"));

            if (!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
                throw new AccessDeniedException("You are not authorized to delete this user");
            }

            userService.disableUser(id);

            return userExtendedMapper.toDTO(user);
        }

    
}
