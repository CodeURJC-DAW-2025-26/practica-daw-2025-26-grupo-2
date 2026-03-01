package es.dawgrupo2.zendashop.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	@PostMapping("/garment/{garmentId}/opinion/new")
	public String newOpinion(Model model,
		@PathVariable long garmentId, Opinion opinion, HttpServletRequest request) {
		Optional<Garment> op = garmentService.findById(garmentId);
		String errorMsg = opinionService.validateFields(opinion);
		if (!errorMsg.isEmpty()) {
			model.addAttribute("message", errorMsg);
			model.addAttribute("backLink", "/garment/" + garmentId);
			return "customError";
		}
		if (op.isPresent()) {
			Garment garment = op.get();
			garment.addOpinion(opinion);
			// String userEmail = principal.getName();
			// String userEmail = "juan@example.com";
			String userEmail = request.getUserPrincipal().getName();
			userService.findByEmail(userEmail).ifPresent(user -> user.addOpinion(opinion));
			opinionService.save(opinion); // Not necessary if cascade is set, but it ensures the opinion is saved
			return "redirect:/garment/" + garmentId + "#opinionForm";
		} else {
			model.addAttribute("message", "Prenda no encontrada.");
			model.addAttribute("backLink", "/garments");
			return "customError";
		}
	}

	@PostMapping("/garment/{garmentId}/opinion/{opinionId}/form")
	public String editOpinion(Model model, @PathVariable long garmentId, @PathVariable long opinionId,
			HttpServletRequest request) {

		Optional<Opinion> op = opinionService.findById(opinionId);
		String userEmail = request.getUserPrincipal().getName();
		User user = userService.findByEmail(userEmail).orElseThrow();
		if (op.isPresent()) {
			if (!request.isUserInRole("ADMIN") && !op.get().getUser().getId().equals(user.getId())) {
				model.addAttribute("message", "Quieto parao! No tienes permiso para editar esta opinión.");
				model.addAttribute("backLink", "/garment/" + garmentId);
				return "customError";
			}
			Opinion opinion = op.get();
			for (Opinion opi : opinion.getGarment().getOpinions()) {
				opi.setStarsRating("★".repeat(opi.getRating()) + "☆".repeat(5 - opi.getRating()));
				opi.setOwn(request.getUserPrincipal() != null && (request.isUserInRole("ADMIN")
						|| opi.getUser().getEmail().equals(request.getUserPrincipal().getName())));
			}
			model.addAttribute("opinion", opinion);
			model.addAttribute("garment", opinion.getGarment());
			return "show_garment";
		} else {
			model.addAttribute("message", "Opinión no encontrada.");
			model.addAttribute("backLink", "/garment/" + garmentId);
			return "customError";
		}

	}

	@PostMapping("/garment/{garmentId}/opinion/edit")
	public String editOpinionProcess(Model model, Opinion editedOpinion, @PathVariable Long garmentId,
			HttpServletRequest request) {

		Optional<Opinion> op = opinionService.findById(editedOpinion.getId());
		String userEmail = request.getUserPrincipal().getName();
		User user = userService.findByEmail(userEmail).orElseThrow();
		if (op.isPresent()) {
			if (!request.isUserInRole("ADMIN") && !op.get().getUser().getId().equals(user.getId())) {
				model.addAttribute("message", "Quieto parao! No tienes permiso para editar esta opinión.");
				model.addAttribute("backLink", "/garment/" + garmentId);
				return "customError";
			}

			Opinion originalOpinion = op.get();

			originalOpinion.setRating(editedOpinion.getRating());
			originalOpinion.setComment(editedOpinion.getComment());
			opinionService.save(originalOpinion);
			return "redirect:/garment/" + originalOpinion.getGarment().getId();
		} else {
			model.addAttribute("message", "Opinión no encontrada.");
			model.addAttribute("backLink", "/garment/" + garmentId);
			return "customError";
		}
	}

	@PostMapping("/garment/{garmentId}/opinion/{opinionId}/delete")
	public String deleteOpinion(Model model, @PathVariable long garmentId, @PathVariable long opinionId,
			HttpServletRequest request) {

		Optional<Opinion> opinion = opinionService.findById(opinionId);

		if (opinion.isPresent()) {
			String userEmail = request.getUserPrincipal().getName();
			User user = userService.findByEmail(userEmail).orElseThrow();
			if (!request.isUserInRole("ADMIN") && !opinion.get().getUser().getId().equals(user.getId())) {
				model.addAttribute("message", "Quieto parao! No tienes permiso para eliminar esta opinión.");
				model.addAttribute("backLink", "/garment/" + garmentId);
				return "customError";
			}

			Garment garment = opinion.get().getGarment();
			garmentId = garment.getId(); // Use reference in opinion to avoid problems with url
			garment.removeOpinion(opinion.get());
			user.removeOpinion(opinion.get());
			opinionService.delete(opinionId);
			return "redirect:/garment/" + garmentId;
		} else {
			model.addAttribute("message", "¿Qué buscabas? Opinión no encontrada.");
			model.addAttribute("backLink", "/garment/" + garmentId);
			return "customError";
		}
	}
}