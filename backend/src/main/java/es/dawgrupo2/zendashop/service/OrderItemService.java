package es.dawgrupo2.zendashop.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.OrderItem;
import es.dawgrupo2.zendashop.repository.GarmentRepository;
import es.dawgrupo2.zendashop.repository.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;

	@Autowired
	private GarmentRepository garmentRepository;

	public Optional<OrderItem> findById(long id) {
		return repository.findById(id);
	}

	public List<OrderItem> findById(List<Long> ids) {
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

	public OrderItem delete(long id) {
		Optional<OrderItem> orderItem = repository.findById(id);
		orderItem.ifPresent(repository::delete);
		return orderItem.orElse(null);
	}

	public long deleteByOrderCompletedFalseAndGarment_Id(Long garmentId) {
		return repository.deleteByOrderCompletedFalseAndGarment_Id(garmentId);
	}

	public Page<OrderItem> findByOrderId(Long orderId, Pageable pageable) {
		return repository.findByOrder_Id(orderId, pageable);
	}

	public OrderItem createOrderItem(Order order, OrderItem orderItem) {
		if (orderItem.getId() != null) {
			throw new IllegalArgumentException("El ID del nuevo pedido no debe ser proporcionado");
		}
		Garment garment = garmentRepository.findById(orderItem.getGarment().getId())
				.orElseThrow(() -> new NoSuchElementException("La prenda especificada no existe"));
		orderItem.setGarment(garment);
		orderItem.setOrder(order);
		order.addOrderItem(orderItem);
		return repository.save(orderItem);
	}

	public OrderItem updateOrderItem(OrderItem originalOrderItem, OrderItem updatedOrderItem) {
		Garment garment = garmentRepository.findById(updatedOrderItem.getGarment().getId())
				.orElseThrow(() -> new NoSuchElementException("La prenda especificada no existe"));
		originalOrderItem.setGarment(garment);
		originalOrderItem.setQuantity(updatedOrderItem.getQuantity());
		originalOrderItem.setSize(updatedOrderItem.getSize());
		return repository.save(originalOrderItem);
	}

	public String validateFields(OrderItem orderItem) {
		String errorMsg = "";
		if (orderItem.getGarment() == null) {
			errorMsg += "La prenda a incluir en el pedido no puede ser nula,";
		}
		if (orderItem.getQuantity() <= 0) {
			errorMsg += " La cantidad debe ser mayor que 0,";
		}
		if (orderItem.getSize() == null || orderItem.getSize().isBlank()) {
			errorMsg += " La talla no puede estar vacía,";
		}
		if (orderItem.getGarment() != null && orderItem.getGarment().getId() == null) {
			errorMsg += " El ID de la prenda no puede ser nulo.";
		}
		return errorMsg.isEmpty() ? null : errorMsg;
	}
}