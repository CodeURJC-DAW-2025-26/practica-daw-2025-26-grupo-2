package es.dawgrupo2.zendashop.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.OpinionService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class GarmentController {

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private OpinionService opinionService;

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String showGarments(@RequestParam(required = false) String nameSearch,
			@RequestParam(required = false) String categorySearch, @RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice, Model model,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest request) {

		// Add first page of garments with filters to the model
		model.addAttribute("garments", garmentService.findAvailableGarmentsByOptionalFilters(nameSearch, categorySearch,
				minPrice, maxPrice, pageable));
		// If the user is logged in, add personalized offers to the model
		if (request.getUserPrincipal() != null) {
			String userEmail = request.getUserPrincipal().getName();
			userService.findByEmail(userEmail).ifPresent(
					user -> model.addAttribute("offers", garmentService.findSmartRecommendations(user.getId())));
		}

		// Convert the pageable sort to a string to maintain the sorting in the "load more" link
		String sortParam = "";
		if (pageable.getSort().isSorted()) {
			sortParam = pageable.getSort().stream()
					.map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
					.findFirst()
					.orElse("");
		}

		// Add the "load more" link including the current filters, sorting and next page number, and whether there are more garments to load to the model
		model.addAttribute("loadMoreLink",
				"/loadMoreGarments?nameSearch=" + (nameSearch != null ? nameSearch : "") + "&categorySearch="
						+ (categorySearch != null ? categorySearch : "") + "&minPrice="
						+ (minPrice != null ? minPrice : "")
						+ "&maxPrice=" + (maxPrice != null ? maxPrice : "") + "&sort=" + sortParam + "&page="
						+ pageable.next().getPageNumber());

		model.addAttribute("hasMore", garmentService
				.findAvailableGarmentsByOptionalFilters(nameSearch, categorySearch, minPrice, maxPrice, pageable.next())
				.hasContent());

		return "index";
	}

	@GetMapping("/loadMoreGarments") // This method is called by AJAX when the user clicks the "load more" button
	// It is possible to unify both methods / and /loadMoreGarments by considering request headers, but we do it like this
	// to enhance readability and maintainability, as the logic for loading more garments is different from the logic for loading the first page (for example, it does not need to load personalized offers)
	public String loadMoreGarments(@RequestParam(required = false) String nameSearch,
			@RequestParam(required = false) String categorySearch, @RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice, Model model,
			@PageableDefault(size = 10) Pageable pageable, HttpServletResponse response) {
		
		model.addAttribute("garments", garmentService.findAvailableGarmentsByOptionalFilters(nameSearch, categorySearch,
				minPrice, maxPrice, pageable));
		// Add whether there are more garments to load to the response headers, so the javascript code can know whether to show the "load more" button or not
		response.addHeader("X-Has-More", String.valueOf(garmentService
				.findAvailableGarmentsByOptionalFilters(nameSearch, categorySearch, minPrice, maxPrice, pageable.next())
				.hasContent()));
		return "garments_cards"; // This is a fragment of the index.html template that only contains the garment cards, so it can be loaded by AJAX and replace the existing cards without reloading the whole page
	}

	@GetMapping("/garment/{id}")
	public String showGarment(Model model, @PathVariable long id, HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable) {

		Optional<Garment> op = garmentService.findById(id);

		if (op.isPresent()) {
			Garment garment = op.get();
			Page<Opinion> opinions = opinionService.findByGarmentId(id, pageable);

			// For each opinion of the garment, set whether the logged in user is the owner of the opinion or an admin, so the view can decide whether to show the edit and delete buttons for each opinion
			for (Opinion opinion : opinions) {
				opinion.setOwn(request.getUserPrincipal() != null && (request.isUserInRole("ADMIN")
						|| opinion.getUser().getEmail().equals(request.getUserPrincipal().getName())));
			}
			
			model.addAttribute("hasMore", opinionService
				.findByGarmentId(id, pageable.next())
				.hasContent());
			model.addAttribute("garment", garment);
			model.addAttribute("opinionsPage", opinions);
			return "show_garment";
		} else {
			model.addAttribute("message", "¿Qué buscabas? Prenda no encontrada");
			model.addAttribute("backLink", "/");
			return "customError";
		}
	}

	@GetMapping("/garment/new")
	public String showGarmentForm(Model model) {
		return "garment_form";
	}

	@PostMapping("/garment/new")
	public String newGarment(Model model, Garment garment, MultipartFile imageFile, HttpServletRequest request) {

		// Backend validation of fields, including the image file, to prevent users from bypassing frontend validation
		// As it is a new garment, image is required, so we pass true as the third parameter of validateFields
		String errorMsg = garmentService.validateFields(garment, imageFile, true);
		// If there are validation errors, show the error page with a link to go back to the garment form
		if (!errorMsg.isEmpty()) {
			model.addAttribute("message", errorMsg);
			model.addAttribute("backLink", "/garment/new");
			return "customError";
		}
		try {
			garmentService.save(garment, imageFile);
		} catch (IOException e) {
			throw new RuntimeException("Error al guardar la imagen", e);
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
			model.addAttribute("message", "¿Qué buscabas? Prenda no encontrada");
			model.addAttribute("backLink", "/");
			return "customError";
		}
	}

	@PostMapping("/garment/{id}/edit")
	public String editGarmentProcess(Model model, Garment editedGarment, @PathVariable long id,
		MultipartFile imageFile, @RequestParam(name = "updateImage", defaultValue = "false") boolean updateImage, HttpServletRequest request) {
		Optional<Garment> op = garmentService.findById(id);

		// Not necessary to check if the user has permission to edit the garment
		// as only admins can access this link (Spring Security configuration ensures that)
		
		// Backend validation of fields, including the image file if updateImage is true, to prevent users from bypassing frontend validation
		// As it is an edit of an existing garment, image is not required, so we pass the value of updateImage as the third parameter of validateFields,
		// so the image file will only be validated if the user has chosen to update the image
		String errorMsg = garmentService.validateFields(editedGarment, imageFile, updateImage);
		if (!errorMsg.isEmpty()) {
			model.addAttribute("message", errorMsg);
			model.addAttribute("backLink", "/garment/" + id + "/edit");
			return "customError";
		}

		if (op.isPresent()) {
			Garment originalGarment = op.get();
			// Update the fields of the original garment with the values of the edited garment
			// and save the original garment, as it is a persistent entity, so it will be updated in the database
			garmentService.setFieldsAndSave(originalGarment, editedGarment, updateImage, imageFile);
			return "redirect:/garment/" + originalGarment.getId();
		} else {
			model.addAttribute("message", "¿Qué buscabas? Prenda no encontrada");
			model.addAttribute("backLink", "/");
			return "customError";
		}
	}

	@PostMapping("/garment/{id}/delete")
	public String deleteGarment(Model model, @PathVariable long id) {

		Optional<Garment> op = garmentService.findById(id);

		// Not necessary to check if the user has permission to delete the garment
		// as only admins can access this link (Spring Security configuration ensures that)

		if (op.isPresent()) {
			Garment garment = op.get();
			// To avoid issues with completed orders, we do not delete the garment, 
			// but we set it as unavailable, so it will not be shown in the store and cannot be bought, 
			// but the existing orders with this garment will not be affected
			garmentService.disable(garment);
			return "redirect:/";
		} else {
			model.addAttribute("message", "¿Qué buscabas? Prenda no encontrada");
			model.addAttribute("backLink", "/");
			return "customError";
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