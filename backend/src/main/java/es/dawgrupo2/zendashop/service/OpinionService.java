package es.dawgrupo2.zendashop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.repository.OpinionRepository;

@Service
public class OpinionService {

	@Autowired
	private OpinionRepository repository;

	@Autowired
	private UserService userService;

	public Optional<Opinion> findById(long id) {
		return repository.findById(id);
	}

	public List<Opinion> findById(List<Long> ids) {
		return repository.findAllById(ids);
	}

	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<Opinion> findAll() {
		return repository.findAll();
	}

	public void save(Opinion opinion) {
		repository.save(opinion);
	}

	public Opinion create(Opinion opinion, Garment garment, User user) {

		if (opinion.getId() != null) {
			throw new IllegalArgumentException("El ID de la opinión no debe ser proporcionado");
		}

		garment.addOpinion(opinion);
		opinion.setGarment(garment);
		user.addOpinion(opinion);
		opinion.setUser(user);
		save(opinion); // Not necessary if cascade is set, but it ensures the opinion is saved

		return opinion;
	}

    public Opinion delete(long id) {
        Opinion opinion = repository.findById(id).orElseThrow();
        repository.deleteById(id);
        return opinion;
    }

	public String validateFields(Opinion opinion) {
		String errorMsg = "";
		if (opinion.getRating() == null || !(opinion.getRating() instanceof Integer) || opinion.getRating() < 1
				|| opinion.getRating() > 5) {
			errorMsg += "La valoración debe ser un número entre 1 y 5. ";
		}
		if (opinion.getComment() == null || opinion.getComment().isEmpty() || opinion.getComment().length() > 50
				|| opinion.getComment().length() < 5) {
			errorMsg += "El comentario no puede estar vacío ni tener más de 50 caracteres ni menos de 5.";
		}
		return errorMsg;
	}

	public Page<Opinion> findByGarmentId(long garmentId, Pageable pageable) {
		return repository.findByGarment_Id(garmentId, pageable);
	}

	public Opinion updateOpinion(Opinion originalOpinion, Opinion editedOpinion) {
		originalOpinion.setRating(editedOpinion.getRating());
		originalOpinion.setComment(editedOpinion.getComment());
		save(originalOpinion);
		return originalOpinion;
	}

	public Opinion removeOpinion(Opinion opinion) {
		Garment garment = opinion.getGarment();
		garment.removeOpinion(opinion);
		User user = opinion.getUser();
		user.removeOpinion(opinion);
		return delete(opinion.getId());
	}
}