package es.dawgrupo2.zendashop.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.OpinionService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OpinionController {

    @Autowired
	private GarmentService garmentService;

	@Autowired
	private OpinionService opinionService;

	@Autowired
	private UserService userService;

    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		//Principal principal = request.getUserPrincipal();

		//if (principal != null) {

			model.addAttribute("logged", true);
			//model.addAttribute("userName", principal.getName());
			model.addAttribute("userName", "ADMIN");
			model.addAttribute("admin", true);

		//} else {
			//model.addAttribute("logged", false);
		//}
	}

	@PostMapping("/garment/{garmentId}/opinion/new")
	public String newOpinion(Model model, 
		//Principal principal, 
		@PathVariable long garmentId, Opinion opinion) {
		Optional<Garment> op = garmentService.findById(garmentId);
		if (op.isPresent()) {
			Garment garment = op.get();
			garment.addOpinion(opinion);
			//String userEmail = principal.getName();
			String userEmail = "juan@example.com";
			userService.findByEmail(userEmail).ifPresent(user -> user.addOpinion(opinion));
			opinionService.save(opinion); // Not necessary if cascade is set, but it ensures the opinion is saved
			return "redirect:/garment/" + garmentId + "#opinionForm";
		} else {
			model.addAttribute("element", "Prenda");
			model.addAttribute("masculine", true);
			return "not_found";
		}
	}

	@GetMapping("/garment/{garmentId}/opinion/{opinionId}/edit")
	public String editOpinion(Model model, @PathVariable long garmentId ,@PathVariable long opinionId) {

		Optional<Opinion> op = opinionService.findById(opinionId);
		if (op.isPresent()) {
			Opinion opinion = op.get();
			model.addAttribute("opinion", opinion);
			model.addAttribute("garment", opinion.getGarment());
			return "show_garment";
		} else {
			model.addAttribute("element", "Opinión");
            model.addAttribute("masculine", false);
			return "not_found";
		}
	}

	@PostMapping("/garment/{garmentId}/opinion/edit")
	public String editOpinionProcess(Model model, Opinion editedOpinion, @PathVariable Long garmentId) {

		Optional<Opinion> op = opinionService.findById(editedOpinion.getId());
		if (op.isPresent()) {
			Opinion originalOpinion = op.get();
			originalOpinion.setRating(editedOpinion.getRating());
			originalOpinion.setComment(editedOpinion.getComment());
			opinionService.save(originalOpinion);
			return "redirect:/garment/" + originalOpinion.getGarment().getId();
		} else {
			model.addAttribute("element", "Opinión");
			model.addAttribute("masculine", false);
			return "not_found";
		}
	}

	@PostMapping("/garment/{garmentId}/opinion/{opinionId}/delete")
	public String deleteOpinion(Model model, @PathVariable long garmentId, @PathVariable long opinionId) {

		Optional<Opinion> opinion = opinionService.findById(opinionId);

		if (opinion.isPresent()) {
            Garment garment = opinion.get().getGarment();
			garmentId = garment.getId(); // Use reference in opinion to avoid problems with url
			User user = opinion.get().getUser();
			// Validate if the user is the owner of the opinion or an admin before allowing deletion
            garment.removeOpinion(opinion.get());
			user.removeOpinion(opinion.get());
			opinionService.delete(opinionId);
			return "redirect:/garment/" + garmentId;
		} else {
			return "not_found";
		}
	}
}