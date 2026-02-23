package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.service.OrderService;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.repository.GarmentRepository;

@Service
public class GarmentService {

	@Autowired
	private GarmentRepository repository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OpinionService opinionService;

	public Optional<Garment> findById(long id) {
		return repository.findById(id);
	}

	public List<Garment> findById(List<Long> ids){
		return repository.findAllById(ids);
	}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public Page<Garment> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

    public void save(Garment garment) {
        repository.save(garment);
    }

	public void save(Garment garment, MultipartFile imageFile) throws IOException{
		if(!imageFile.isEmpty()) {
			try {
				garment.setImage(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		this.save(garment);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	public long getCount() {
		return repository.count();
	}

	public Page<Garment> findByAvailableTrue(Pageable pageable) {
		return repository.findByAvailableTrue(pageable);
	}

	public void disable(Garment garment) {
		garment.setAvailable(false);
		// TODO: Remove opinions
		//for (Opinion opinion: garment.getOpinions()) {
			//garment.removeOpinion(opinion);
			//opinionService.deleteBy(opinion.getId());
		//}
		orderService.disableGarmentInCarts(garment);
		save(garment);
	}

	public Page<Garment> findAvailableGarmentsByOptionalFilters(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
		return repository.findAvailableGarmentsByOptionalFilters(name, category, minPrice, maxPrice, pageable);
	}
}