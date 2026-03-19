package es.dawgrupo2.zendashop.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.basicDTO.GarmentBasicDTO;
import es.dawgrupo2.zendashop.extendedDTO.GarmentExtendedDTO;
import es.dawgrupo2.zendashop.basicDTO.GarmentBasicMapper;
import es.dawgrupo2.zendashop.basicDTO.ImageDTO;
import es.dawgrupo2.zendashop.basicDTO.ImageMapper;
import es.dawgrupo2.zendashop.extendedDTO.GarmentExtendedMapper;
import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.model.Image;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.UserService;
import es.dawgrupo2.zendashop.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/garments")
public class GarmentRestController {

	@Autowired
	private GarmentService garmentService;
	
    @Autowired
    private UserService userService;

    @Autowired
	private ImageService imageService;

	@Autowired
	private GarmentBasicMapper garmentBasicMapper;

    @Autowired
    private GarmentExtendedMapper garmentExtendedMapper;

    @Autowired
	private ImageMapper imageMapper;

    /* 
	@GetMapping("/")
	public Page<GarmentBasicDTO> getGarments(@PageableDefault(size = 10) Pageable pageable) {
        return garmentService.findAll(pageable).map(garmentBasicMapper::toDTO);
	}
    */

    @GetMapping("/")
	public Page<GarmentBasicDTO> getGarments(@RequestParam(required = false) String nameSearch,
			@RequestParam(required = false) String categorySearch, @RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice, Model model,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {

        return garmentService.findAvailableGarmentsByOptionalFilters(nameSearch, categorySearch,
				minPrice, maxPrice, pageable).map(garmentBasicMapper::toDTO);
        
        /*
        List<Garment> garmentOffers = new ArrayList<Garment>();
        if (request.getUserPrincipal() != null) {
            String userEmail = request.getUserPrincipal().getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isPresent()) {
                garmentOffers = garmentService.findSmartRecommendations(userOpt.get().getId());
            }
        }
		for (Garment garment : garmentOffers) {
			garments.add(garmentBasicMapper.toDTO(garment));
		}

		return garments;
        */
    }

	// If the user is logged in, show personalized offers
    @GetMapping("/offers")
    public List<GarmentBasicDTO> getOffers(HttpServletRequest request){
        List<Garment> garments = new ArrayList<Garment>();
        if (request.getUserPrincipal() != null) {
            String userEmail = request.getUserPrincipal().getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isPresent()) {
                garments = garmentService.findSmartRecommendations(userOpt.get().getId());
            }
        }
        return garmentBasicMapper.toDTOs(garments);
    }

	@GetMapping("/{id}")
	public GarmentExtendedDTO getGarment(@PathVariable long id) {
        return garmentExtendedMapper.toDTO(garmentService.findById(id).orElseThrow());
	}

	@PostMapping("/")
	public ResponseEntity<GarmentExtendedDTO> createGarment(@RequestBody GarmentBasicDTO garmentBasicDTO, HttpServletRequest request) throws AccessDeniedException {
		if (!request.isUserInRole("ADMIN")) {
			throw new AccessDeniedException("Solo un administrador puede añadir prendas");
		}
		Garment garment = garmentBasicMapper.toDomain(garmentBasicDTO);
        
		String errorMsg = garmentService.validateFields(garment);
		if (errorMsg != null && !errorMsg.isEmpty()) {
			throw new IllegalArgumentException(errorMsg);
		}
		garment = garmentService.create(garment);
		GarmentExtendedDTO garmentDTO = garmentExtendedMapper.toDTO(garment);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(garmentDTO.id()).toUri();

		return ResponseEntity.created(location).body(garmentDTO);
	}

    @PostMapping("/{id}/images/")
	public ResponseEntity<ImageDTO> createGarmentImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {
        
        Garment garment = garmentService.findById(id).orElseThrow();

		if (imageFile.isEmpty()) {
			throw new IllegalArgumentException("La imagen no puede estar vacía");
		}
		
        Image image = imageService.createImage(imageFile.getInputStream());
		garmentService.setImage(garment, image);
		
        URI location = fromCurrentContextPath()
				.path("/images/{imageId}/media")
				.buildAndExpand(image.getId())
				.toUri();

		return ResponseEntity.created(location).body(imageMapper.toDTO(image));
	}

    @PutMapping("/{id}")
	public GarmentExtendedDTO replaceGarment(@PathVariable long id, @RequestBody GarmentExtendedDTO updatedGarmentDTO) {
        Garment originalGarment = garmentService.findById(id).orElseThrow();
        Garment updateGarment = garmentExtendedMapper.toDomain(updatedGarmentDTO);

        String errorMsg = garmentService.validateFields(updateGarment);
		if (errorMsg != null && !errorMsg.isEmpty()) {
			throw new IllegalArgumentException(errorMsg);
		}
		return garmentExtendedMapper.toDTO(garmentService.updateGarment(originalGarment, updateGarment));
	}

    @DeleteMapping("/{id}")
	public GarmentExtendedDTO deleteGarment(@PathVariable long id) {
        Garment garment = garmentService.findById(id).orElseThrow();
		return garmentExtendedMapper.toDTO(garmentService.disable(garment));
	}

    @DeleteMapping("/{id}/image")
	public ImageDTO deleteGarmentImage(@PathVariable long id)
			throws IOException {
        Garment garment = garmentService.findById(id).orElseThrow();
        ImageDTO image = imageMapper.toDTO(garment.getImage());
		garment.setImage(null);
		imageService.deleteImage(id);
		garmentService.save(garment);

		return image;
	}
}