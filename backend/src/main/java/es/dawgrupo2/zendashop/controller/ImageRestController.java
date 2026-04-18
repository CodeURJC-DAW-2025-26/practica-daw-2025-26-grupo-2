package es.dawgrupo2.zendashop.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import es.dawgrupo2.zendashop.basicDTO.ImageDTO;
import es.dawgrupo2.zendashop.basicDTO.ImageMapper;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.model.Image;
import es.dawgrupo2.zendashop.service.ImageService;
import es.dawgrupo2.zendashop.service.UserService;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageMapper mapper;

    @GetMapping("/{id}")
    public ImageDTO getImage(@PathVariable long id) {
        return mapper.toDTO(imageService.getImage(id));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<Object> getImageFile(@PathVariable long id, HttpServletRequest request)
            throws SQLException, IOException {
        Image image = imageService.getImage(id);

        if (image.getAvatar()) {
            if (request.getUserPrincipal() == null) {
                throw new AccessDeniedException("Debes estar logueado para ver avatares");
            }

            // If someone is logged, we check who is the user
            User user = userService.findByEmail(request.getUserPrincipal().getName())
                    .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

            if (!request.isUserInRole("ADMIN")) {
                if (user.getAvatar() == null || !user.getAvatar().getId().equals(id)) {
                    throw new AccessDeniedException("No puedes acceder al avatar de otro usuario");
                }
            }
        }

        Resource imageFile = imageService.getImageFile(id);

        MediaType mediaType = MediaTypeFactory
                .getMediaType(imageFile)
                .orElse(MediaType.IMAGE_JPEG);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(imageFile);
    }

    @PutMapping("/{id}/media")
    public ResponseEntity<Object> replaceImageFile(@PathVariable long id,
            @RequestParam MultipartFile imageFile, HttpServletRequest request) throws IOException {
        User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        if (!request.isUserInRole("ADMIN") && (user.getAvatar() == null || !user.getAvatar().getId().equals(id))) {
			throw new AccessDeniedException("No tienes permisos de edición de imágenes aquí");
		}
        
        imageService.replaceImageFile(id, imageFile.getInputStream());
        return ResponseEntity.noContent().build();
    }
}