package es.dawgrupo2.zendashop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.repository.OpinionRepository;


@Service
public class OpinionService {

	@Autowired
	private OpinionRepository repository;

	public Optional<Opinion> findById(long id) {
		return repository.findById(id);
	}

	public List<Opinion> findById(List<Long> ids){
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

	public void delete(long id) {
		repository.deleteById(id);
	}
}