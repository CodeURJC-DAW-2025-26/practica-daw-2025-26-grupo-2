package es.dawgrupo2.zendashop.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GarmentController {

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private UserService userService;

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

		String userEmail = principal.getName();
		User user = userService.findByEmail(userEmail).orElseThrow();
    	Long userID = user.getId();

		model.addAttribute("logged", true);
		model.addAttribute("userID", userID);
		model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
		model.addAttribute("logged", false);
		model.addAttribute("userID", null);
		}
	}

	@GetMapping("/")
	public String showGarments(@RequestParam(required = false) String nameSearch,
			@RequestParam(required = false) String categorySearch, @RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice, Model model, @PageableDefault(size = 10) Pageable page, @ModelAttribute("userID") Long userID) {

		model.addAttribute("garments", garmentService.findAvailableGarmentsByOptionalFilters(nameSearch, categorySearch, minPrice, maxPrice, page));
		if (userID != null) {
        model.addAttribute("offers", garmentService.findSmartRecommendations(userID));
    }

		return "index";
		}

	@GetMapping("/garment/{id}")
	public String showGarment(Model model, @PathVariable long id) {

		Optional<Garment> op = garmentService.findById(id);

		if (op.isPresent()) {
			Garment garment = op.get();
			model.addAttribute("garment", garment);
			return "show_garment";
		} else {
			model.addAttribute("element", "Prenda");
			model.addAttribute("masculine", false);
			return "not_found";
		}
	}

	@GetMapping("/garment/new")
	public String showGarmentForm(Model model) {
		return "garment_form";
	}

	@PostMapping("/garment/new")
	public String newGarment(Model model, Garment garment, MultipartFile imageFile) {

		if (!imageFile.isEmpty()) {
			try {
				garmentService.save(garment, imageFile);
			} catch (IOException e) {
				throw new RuntimeException("Error al guardar la imagen", e);
			}
		}

		return "redirect:/garment/" + garment.getId();
	}

	@GetMapping("/garment/{id}/edit")
	public String editGarment(Model model, @PathVariable long id) {

		Optional<Garment> op = garmentService.findById(id);
		if (op.isPresent()) {
			Garment garment = op.get();
			model.addAttribute("garment", garment);
			return "garment_form";
		} else {
			model.addAttribute("element", "Prenda");
			model.addAttribute("masculine", false);
			return "not_found";
		}
	}

	@PostMapping("/garment/{id}/edit")
	public String editGarmentProcess(Model model, Garment editedGarment, @PathVariable long id,
			MultipartFile imageFile) {

		Optional<Garment> op = garmentService.findById(id);
		if (op.isPresent()) {
			Garment originalGarment = op.get();
			if (editedGarment.getCategory() == null || editedGarment.getCategory().trim().isEmpty()) {
				editedGarment.setCategory(originalGarment.getCategory());
			}
			originalGarment.setName(editedGarment.getName());
			originalGarment.setCategory(editedGarment.getCategory());
			originalGarment.setPrice(editedGarment.getPrice());
			originalGarment.setDescription(editedGarment.getDescription());
			originalGarment.setFeatures(editedGarment.getFeatures());
			try {
				garmentService.save(originalGarment, imageFile);
			} catch (IOException e) {
				throw new RuntimeException("Error al guardar la imagen", e);
			}
			return "redirect:/garment/" + originalGarment.getId();
		} else {
			return "not_found";
		}
	}

	@PostMapping("/garment/{id}/delete")
	public String deleteGarment(Model model, @PathVariable long id) {

		Optional<Garment> op = garmentService.findById(id);

		if (op.isPresent()) {
			Garment garment = op.get();
			garmentService.disable(garment);
			return "redirect:/";
		} else {
			model.addAttribute("element", "Prenda");
			model.addAttribute("masculine", false);
			return "not_found";
		}
	}

	@GetMapping("/garment/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Garment> op = garmentService.findById(id);

		if (op.isPresent() && op.get().getImage() != null) {

			Blob image = op.get().getImage();
			Resource imageFile = new InputStreamResource(image.getBinaryStream());

			MediaType mediaType = MediaTypeFactory
					.getMediaType(imageFile)
					.orElse(MediaType.IMAGE_JPEG);

			return ResponseEntity
					.ok()
					.contentType(mediaType)
					.body(imageFile);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}