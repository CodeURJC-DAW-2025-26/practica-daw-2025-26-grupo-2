package es.dawgrupo2.zendashop.controller;

import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.repository.GarmentRepository;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GarmentController {

	@Autowired
	private GarmentRepository garmentRepository;

    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/")
	public String showGarments(Model model) {

		model.addAttribute("garments", garmentRepository.findAll());

		return "index";
	}

	@GetMapping("/garment/{id}")
	public String showGarment(Model model, @PathVariable long id) {

		Optional<Garment> op = garmentRepository.findById(id);

		if (op.isPresent()) {
			Garment garment = op.get();
			model.addAttribute("garment", garment);
			return "show_garment";	
		} else {
            model.addAttribute("element", "Prenda");
            model.addAttribute("masculine", false);
			return "garment_not_found";
		}
	}

	@GetMapping("/garment/form")
	public String showGarmentForm(Model model) {
		return "garment_form";
	}
	

	@PostMapping("/garment/new")
	public String newGarment(Model model, Garment garment) {

		garmentRepository.save(garment);

		return "redirect: /garment/" + garment.getId();
	}

	@GetMapping("/garment/{id}/edit")
	public String editGarment(Model model, @PathVariable long id) {

		Optional<Garment> op = garmentRepository.findById(id);
		if (op.isPresent()) {
			Garment garment = op.get();
			model.addAttribute("garment", garment);
			return "garment_form";
		} else {
			model.addAttribute("element", "Prenda");
            model.addAttribute("masculine", false);
			return "garment_not_found";
		}
	}

	@PostMapping("/garment/edit")
	public String editGarmentProcess(Model model, Garment editedGarment) {

		Optional<Garment> op = garmentRepository.findById(editedGarment.getId());
		if (op.isPresent()) {
			garmentRepository.save(editedGarment);
			return "redirect: /garment/" + editedGarment.getId();
		} else {
			return "garment_not_found";
		}
	}

	@PostMapping("/garment/{id}/delete")
	public String deleteGarment(Model model, @PathVariable long id) {

		Optional<Garment> garment = garmentRepository.findById(id);

		if (garment.isPresent()) {
			garmentRepository.deleteById(id);
			return "redirect: /";
		} else {
			return "garment_not_found";
		}
	}

	@GetMapping("/garment/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Garment> op = garmentRepository.findById(id);

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