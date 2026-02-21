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
import es.dawgrupo2.zendashop.repository.OpinionRepository;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OpinionController {

	@Autowired
	private OpinionRepository opinionRepository;

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

	@PostMapping("/opinion/new")
	public String newOpinion(Model model, Opinion opinion) {

		opinionRepository.save(opinion);

		return "redirect: /garment/" + opinion.getGarment().getId();
	}

	@GetMapping("/opinion/{id}/edit")
	public String editOpinion(Model model, @PathVariable long id) {

		Optional<Opinion> op = opinionRepository.findById(id);
		if (op.isPresent()) {
			Opinion opinion = op.get();
			model.addAttribute("opinion", opinion);
			return "opinion_form";
		} else {
			model.addAttribute("element", "Opinión");
            model.addAttribute("masculine", false);
			return "opinion_not_found";
		}
	}

	@PostMapping("/opinion/edit")
	public String editOpinionProcess(Model model, Opinion editedOpinion) {

		Optional<Opinion> op = opinionRepository.findById(editedOpinion.getId());
		if (op.isPresent()) {
			opinionRepository.save(editedOpinion);
			return "redirect: /garment/" + editedOpinion.getGarment().getId();
		} else {
			return "not_found";
		}
	}

	@PostMapping("/opinion/{id}/delete")
	public String deleteOpinion(Model model, @PathVariable long id) {

		Optional<Opinion> opinion = opinionRepository.findById(id);

		if (opinion.isPresent()) {
            Garment garment = opinion.get().getGarment();
            garment.removeOpinion(opinion.get());
			opinionRepository.deleteById(id);
			return "redirect: /garment/" + garment.getId();
		} else {
			return "not_found";
		}
	}
}