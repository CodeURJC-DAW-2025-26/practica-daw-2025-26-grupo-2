package es.dawgrupo2.zendashop.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.repository.GarmentRepository;

@Controller
public class GarmentController {

	@Autowired
	private GarmentRepository garmentRepository;

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

	@PostMapping("/garment/new")
	public String newGarment(Model model, Garment garment) {

		garmentRepository.save(garment);

		return "saved_garment";
	}

	@GetMapping("/editgarment/{id}")
	public String editGarment(Model model, @PathVariable long id) {

		Optional<Garment> op = garmentRepository.findById(id);
		if (op.isPresent()) {
			Garment garment = op.get();
			model.addAttribute("garment", garment);
			return "garment_form";
		} else {
			return "garment_not_found";
		}
	}

	@PostMapping("/editgarment")
	public String editGarmentProcess(Model model, Garment editedGarment) {

		Optional<Garment> op = garmentRepository.findById(editedGarment.getId());
		if (op.isPresent()) {
			garmentRepository.save(editedGarment);
			model.addAttribute("garment", editedGarment);
			return "edited_garment";
		} else {
			return "garment_not_found";
		}
	}

	@PostMapping("/garment/{id}/delete")
	public String deleteGarment(Model model, @PathVariable long id) {

		Optional<Garment> garment = garmentRepository.findById(id);

		if (garment.isPresent()) {
			garmentRepository.deleteById(id);
			return "deleted_garment";
		} else {
			return "garment_not_found";
		}
	}

}