package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.repository.GarmentRepository;

import es.dawgrupo2.zendashop.service.OrderService;


@Service
public class GarmentService {

	@Autowired
	private GarmentRepository repository;

	@Autowired
	private OrderService orderService;

	public Optional<Garment> findById(long id) {
		return repository.findById(id);
	}

	public List<Garment> findById(List<Long> ids){
		return repository.findAllById(ids);
	}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<Garment> findAll() {
		return repository.findAll();
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
		List<Order> carts = orderService.findByCompletedFalse();
		for (Order cart : carts) {
			if (cart.getOrderItems().isEmpty()){
				cart.getUser().setCart(null);
				orderService.delete(cart.getId());
			}
			else{
				cart.updateTotalPrice();
				orderService.save(cart);
			}
		}
	}
}