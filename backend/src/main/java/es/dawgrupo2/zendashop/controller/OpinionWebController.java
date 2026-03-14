package es.dawgrupo2.zendashop.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.OpinionService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class OpinionWebController {

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private OpinionService opinionService;

	@Autowired
	private UserService userService;

	@GetMapping("/loadMoreOpinions")
	public String loadMoreOpinions(Model model, @RequestParam long id, @PageableDefault(size = 10) Pageable pageable,
			HttpServletResponse response, HttpServletRequest request) {
		Page<Opinion> opinions = opinionService.findByGarmentId(id, pageable);

		
		for (Opinion opi : opinions) {
			opi.setOwn(request.getUserPrincipal() != null && (request.isUserInRole("ADMIN")
					|| opi.getUser().getEmail().equals(request.getUserPrincipal().getName())));
		}
		model.addAttribute("opinionsPage", opinions);

		// Add whether there are more opinions to load to the response headers, so the
		// javascript code can know whether to show the "load more" button or not
		response.addHeader("X-Has-More", String.valueOf(opinionService
				.findByGarmentId(id, pageable.next())
				.hasContent()));
		return "opinions_cards"; // This is a fragment of the show_garment.html template that only contains the opinion
									// cards, so it can be loaded by AJAX and replace the existing cards without
									// reloading the whole page
	}

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
			String userEmail = request.getUserPrincipal().getName();
			Garment garment = op.get();
			opinionService.newOpinion(opinion, garment, userEmail);
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
			opinionService.updateOpinion(originalOpinion, editedOpinion);
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

		Optional<Opinion> opOpinion = opinionService.findById(opinionId);

		if (opOpinion.isPresent()) {
			Opinion opinion = opOpinion.get();
			String userEmail = request.getUserPrincipal().getName();
			User user = userService.findByEmail(userEmail).orElseThrow();
			if (!request.isUserInRole("ADMIN") && !opinion.getUser().getId().equals(user.getId())) {
				model.addAttribute("message", "Quieto parao! No tienes permiso para eliminar esta opinión.");
				model.addAttribute("backLink", "/garment/" + garmentId);
				return "customError";
			}
			opinionService.removeOpinion(opinion);
			return "redirect:/garment/" + opinion.getGarmentId();
		} else {
			model.addAttribute("message", "¿Qué buscabas? Opinión no encontrada.");
			model.addAttribute("backLink", "/garment/" + garmentId);
			return "customError";
		}
	}
}