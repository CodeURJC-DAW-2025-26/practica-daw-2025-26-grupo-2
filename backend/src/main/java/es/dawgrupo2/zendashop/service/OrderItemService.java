package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.model.OrderItem;
import es.dawgrupo2.zendashop.repository.OrderItemRepository;


@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;

	public Optional<OrderItem> findById(long id) {
		return repository.findById(id);
	}

	public List<OrderItem> findById(List<Long> ids){
		return repository.findAllById(ids);
	}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<OrderItem> findAll() {
		return repository.findAll();
	}

    public void save(OrderItem orderItem) {
        repository.save(orderItem);
    }

	public void delete(long id) {
		repository.deleteById(id);
	}

	public long deleteByOrderCompletedFalseAndGarment_Id(Long garmentId) {
		return repository.deleteByOrderCompletedFalseAndGarment_Id(garmentId);
	}
}