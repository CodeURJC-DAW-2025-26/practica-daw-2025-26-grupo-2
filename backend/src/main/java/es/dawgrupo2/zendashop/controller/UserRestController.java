package es.dawgrupo2.zendashop.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import es.dawgrupo2.zendashop.model.Image;
import es.dawgrupo2.zendashop.model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.net.URI;
import es.dawgrupo2.zendashop.extendedDTO.OrderExtendedDTO;
import es.dawgrupo2.zendashop.extendedDTO.OrderExtendedMapper;
import es.dawgrupo2.zendashop.service.ImageService;
import es.dawgrupo2.zendashop.service.OrderService;
import es.dawgrupo2.zendashop.basicDTO.ImageDTO;
import es.dawgrupo2.zendashop.basicDTO.ImageMapper;
import es.dawgrupo2.zendashop.basicDTO.OrderBasicMapper;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;



@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserBasicMapper userBasicMapper;

    @Autowired
    private UserExtendedMapper userExtendedMapper;

    @Autowired
    private OrderExtendedMapper orderExtendedMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private OrderBasicMapper orderBasicMapper;

    @Autowired
    private ImageMapper imageMapper;


    // Endpoint to obtain a paginated list of users, excluding disabled ones
    @GetMapping("/")
    public Page<UserBasicDTO> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        return userService.findByDisabledFalse(pageable).map(userBasicMapper::toDTO);
    }

    // Endpoint to register a new user
    @PostMapping("/")
    public ResponseEntity<UserExtendedDTO> registerUser(@RequestBody UserExtendedDTO userDTO){

        User user = userExtendedMapper.toDomain(userDTO);

        // validate the user fields, including password
        String errMsg = userService.validateFields(user, true);
        if (!errMsg.isEmpty()) {
            throw new IllegalArgumentException(errMsg);
        }  

        // validate that the email is not already taken by another user
        if (userService.findByEmail(user.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un usuario registrado con ese email");
        }

        userService.setEncodedPassword(user);
        userService.setUserRoles(user, "USER");
        userService.save(user);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();

		return ResponseEntity.created(location).body(userExtendedMapper.toDTO(user));

    }

    @PostMapping("/{id}/images/")
	public ResponseEntity<ImageDTO> createUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile, HttpServletRequest request) throws IOException {
        
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("No tienes permiso para añadir una imagen a este usuario");
        }

        if (user.getAvatar() != null) {
            throw new IllegalStateException("El usuario ya tiene una imagen asignada, no se pueden asignar dos imágenes a un mismo usuario, edítala o elimínala para cambiarla");
        }

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("La imagen no puede estar vacía");
        }

        if (user.getAvatar() != null) {
            throw new IllegalStateException("El usuario ya tiene una imagen asignada, no se pueden asignar dos imágenes a un mismo usuario, edítala o elimínala para cambiarla");
        }

		if (imageFile.isEmpty()) {
			throw new IllegalArgumentException("La imagen no puede estar vacía");
		}
		
        Image image = imageService.createImage(imageFile.getInputStream());
		userService.setImage(user, image);
		
        URI location = fromCurrentContextPath()
				.path("/images/{imageId}/media")
				.buildAndExpand(image.getId())
				.toUri();

		return ResponseEntity.created(location).body(imageMapper.toDTO(image));
	}

    // Endpoint to obtain a user by id, only if the requester is an admin or the user itself
    @GetMapping("/{id}")
    public UserExtendedDTO getUserById(@PathVariable long id, HttpServletRequest request) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if(!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("No tienes permiso para acceder a este usuario");
        }

        return userExtendedMapper.toDTO(user);
    }

    // Endpoint to update a user, only if the requester is an admin or the user itself
    @PutMapping("/{id}")
    public UserExtendedDTO updateUser(@PathVariable long id, @RequestBody UserExtendedDTO updateUserDTO, HttpServletRequest request) {

        User originalUser = userService.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        // only allow the user itself or an admin to edit the user
        if(!request.isUserInRole("ADMIN") && ! originalUser.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("No tienes permiso para editar este usuario");
        }

        User editedUser = userExtendedMapper.toDomain(updateUserDTO);
        // check if the request comes with a password, if not, keep the original one
        boolean updatePassword = editedUser.getEncodedPassword() != null && !editedUser.getEncodedPassword().isEmpty();

        String errMsg = userService.validateFields(editedUser, updatePassword);
        if (!errMsg.isEmpty()) {
            throw new IllegalArgumentException(errMsg);
        }

        if (!originalUser.getEmail().equals(editedUser.getEmail()) && userService.findByEmail(editedUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email");
        }
        
        userService.updateUser(originalUser, editedUser, updatePassword, false);
        userService.save(originalUser);

        return userExtendedMapper.toDTO(originalUser);
    }

    // Endpoint to delete a user, only if the requester is an admin or the user itself
    @DeleteMapping("/{id}")
    public UserExtendedDTO deleteUser(@PathVariable long id, HttpServletRequest request) {
        User user = userService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("No tienes permiso para eliminar este usuario");
        }

        return userExtendedMapper.toDTO(userService.disableUser(id));
    }

    @DeleteMapping("/{id}/images/{avatarId}")
	public ImageDTO deleteUserImage(@PathVariable long id, @PathVariable long avatarId, HttpServletRequest request)
			throws IOException {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("No tienes permiso para eliminar la imagen de este usuario");
        }

		if (user.getAvatar() == null || user.getAvatar().getId() != avatarId) {
			throw new IllegalStateException("La imagen especificada no está asignada a este usuario");
		}
        ImageDTO image = imageMapper.toDTO(user.getAvatar());
		user.setAvatar(null);
		imageService.deleteImage(avatarId);
		userService.save(user);

		return image;
	}

    // Endpoint to obtain the cart of a user, only if the requester is an admin or the user itself
    @GetMapping("/{id}/cart")
    public OrderExtendedDTO getUserCart(@PathVariable long id, HttpServletRequest request) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if(!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("No tienes permiso para ver el carrito de este usuario");
        }

        if (user.getCart() == null) {
            throw new NoSuchElementException("El usuario no tiene un carrito asignado");
        }

        return orderExtendedMapper.toDTO(user.getCart());
    }

    // Endpoint to obtain the completed orders of a user, only if the requester is an admin or the user itself
    @GetMapping("/{id}/orders")
    public Page<es.dawgrupo2.zendashop.basicDTO.OrderBasicDTO> getUserOrders(@PathVariable long id, @PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
       
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (!request.isUserInRole("ADMIN") && !user.getEmail().equals(request.getUserPrincipal().getName())) {
            throw new AccessDeniedException("No tienes permiso para ver los pedidos de este usuario");
        }

        return orderService.findByUserIdAndCompletedTrue(id, pageable).map(orderBasicMapper::toDTO);
    }

    
    // Endpoint to obtain the current user
    @GetMapping("/me")
    public UserExtendedDTO getCurrentUser(HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return this.getUserById(user.getId(), request);
    }

    // Endpoint to update the current user using the same validation as the general update user endpoint, but without the need to specify the user id in the path 
    @PutMapping("/me")
    public UserExtendedDTO updateCurrentUser(@RequestBody UserExtendedDTO updateUserDTO, HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return this.updateUser(user.getId(), updateUserDTO, request);
    }

    // Endpoint to obtain the cart of the current user, using the same validation as the general get user cart endpoint, but without the need to specify the user id in the path
    @GetMapping("/me/cart")
    public OrderExtendedDTO getCurrentUserCart(HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return this.getUserCart(user.getId(), request);
    }

    // Endpoint to obtain the completed orders of the current user, using the same validation as the general get user orders endpoint, but without the need to specify the user id in the path
    @GetMapping("/me/orders")
    public Page<es.dawgrupo2.zendashop.basicDTO.OrderBasicDTO> getCurrentUserOrders(@PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return this.getUserOrders(user.getId(), pageable, request);
    }
    
    
}
