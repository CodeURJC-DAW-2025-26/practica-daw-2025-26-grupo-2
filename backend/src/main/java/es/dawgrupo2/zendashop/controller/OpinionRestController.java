package es.dawgrupo2.zendashop.controller;


import java.util.NoSuchElementException;

import java.net.URI;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;

import es.dawgrupo2.zendashop.basicDTO.OpinionBasicMapper;
import es.dawgrupo2.zendashop.basicDTO.OpinionBasicDTO;
import es.dawgrupo2.zendashop.extendedDTO.OpinionExtendedDTO;
import es.dawgrupo2.zendashop.extendedDTO.OpinionExtendedMapper;
import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.OpinionService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;


import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/garments/{garmentId}/opinions")
public class OpinionRestController {

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private OpinionService opinionService;

	@Autowired
	private UserService userService;

	@Autowired
	private OpinionBasicMapper opinionBasicMapper;

	@Autowired
	private OpinionExtendedMapper opinionExtendedMapper;

	@GetMapping("/")
	public Page<OpinionBasicDTO> getOpinions(@PageableDefault(size = 10) Pageable pageable,
			@PathVariable long garmentId) {

		return opinionService.findByGarmentId(garmentId, pageable).map(opinionBasicMapper::toDTO);

	}

	@GetMapping("/{opinionId}")
	public OpinionExtendedDTO getOpinion(@PathVariable long opinionId, @PathVariable long garmentId) {
		Opinion opinion = opinionService.findById(opinionId)
				.orElseThrow(() -> new NoSuchElementException("Opinión no encontrada"));
		if (opinion.getGarment().getId() != garmentId) {
			throw new NoSuchElementException("La opinión no pertenece a la prenda especificada");
		}
		return opinionExtendedMapper.toDTO(opinion);
	}

	@PostMapping("/")
	public ResponseEntity<OpinionExtendedDTO> createOpinion(@RequestBody OpinionBasicDTO opinionBasicDTO,
			@PathVariable long garmentId, HttpServletRequest request) throws AccessDeniedException {
				
		Opinion opinion = opinionBasicMapper.toDomain(opinionBasicDTO);
		Garment garment = garmentService.findById(garmentId).orElseThrow(() -> new NoSuchElementException("Prenda no encontrada"));
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		opinion = opinionService.create(opinion, garment, user);
		OpinionExtendedDTO opinionExtendedDTO = opinionExtendedMapper.toDTO(opinion);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(opinion.getId()).toUri();
		return ResponseEntity.created(location).body(opinionExtendedDTO);
	}

	@PutMapping("/{opinionId}")
	public OpinionExtendedDTO replaceOpinion(@PathVariable long opinionId, @PathVariable long garmentId,
			@RequestBody OpinionBasicDTO updatedOpinionBasicDTO, HttpServletRequest request)
			throws AccessDeniedException {

		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		Opinion updatedOpinionBasic = opinionBasicMapper.toDomain(updatedOpinionBasicDTO);
		Opinion originalOpinion = opinionService.findById(opinionId)
				.orElseThrow(() -> new NoSuchElementException("Opinión no encontrada"));

		if (originalOpinion.getGarment().getId() != garmentId) {
			throw new NoSuchElementException("La opinión no pertenece a la prenda especificada");
		}

		if (!request.isUserInRole("ADMIN") && !originalOpinion.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("No tienes permiso para modificar la opinión");
		}
		String errorMsg = opinionService.validateFields(updatedOpinionBasic);
		if (errorMsg != null && !errorMsg.isEmpty()) {
			throw new IllegalArgumentException(errorMsg);
		}
		originalOpinion = opinionService.updateOpinion(originalOpinion, updatedOpinionBasic);
		OpinionExtendedDTO opinionExtendedDTO = opinionExtendedMapper.toDTO(originalOpinion);
		return opinionExtendedDTO;
	}



	@DeleteMapping("/{opinionId}")
	public OpinionExtendedDTO deleteOpinion(@PathVariable long opinionId, @PathVariable long garmentId,
			HttpServletRequest request) throws AccessDeniedException {
		Opinion opinion = opinionService.findById(opinionId).orElseThrow();
		if (opinion.getGarment().getId() != garmentId) {
			throw new NoSuchElementException("La opinión no pertenece a la prenda especificada");
		}
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		if (!request.isUserInRole("ADMIN") && !opinion.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("No tienes permiso para eliminar la opinión");
		}
		return opinionExtendedMapper.toDTO(opinionService.delete(opinionId));
	}

}