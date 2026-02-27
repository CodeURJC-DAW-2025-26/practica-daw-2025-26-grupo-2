package es.dawgrupo2.zendashop.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.OrderItem;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.repository.OrderRepository;
import es.dawgrupo2.zendashop.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderItemService orderItemService;

	public Optional<Order> findById(long id) {
		return repository.findById(id);
	}

	public List<Order> findById(List<Long> ids) {
		return repository.findAllById(ids);
	}

	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<Order> findByCompletedTrue() {
		return repository.findByCompletedTrue();
	}

	public void save(Order order) {
		repository.save(order);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	public List<Order> findByUserIdAndCompletedTrue(Long userId) {
		return repository.findByUserIdAndCompletedTrue(userId);
	}

	public List<Order> findByCompletedFalseAndOrderItems_Garment_Id(long id) {
		return repository.findDistinctByCompletedFalseAndOrderItems_Garment_Id(id);
	}

	@Transactional
	public void disableGarmentInCarts(Garment garment) {
		List<Order> modifiedOrders = findByCompletedFalseAndOrderItems_Garment_Id(garment.getId());

		if (modifiedOrders.isEmpty())
			return;

		orderItemService.deleteByOrderCompletedFalseAndGarment_Id(garment.getId());

		for (Order order : modifiedOrders) {
			order.getOrderItems().removeIf(item -> item.getGarment().getId().equals(garment.getId()));
			Order scopeOrder = repository.findById(order.getId()).orElse(null);
			if (scopeOrder == null)
				continue;
			if (scopeOrder.getOrderItems() == null || scopeOrder.getOrderItems().isEmpty()) {
				deleteCart(scopeOrder);
			} else {
				scopeOrder.updateTotalPrice();
				repository.save(scopeOrder);
			}
		}
	}

	public void deleteCart(Order cart) {
		if (cart == null) {
			return;
		}
		for (OrderItem item : cart.getOrderItems()) {
			orderItemService.delete(item.getId());
		}
		User user = cart.getUser();
		if (user != null) {
			user.setCart(null);
			cart.setUser(null);
			userRepository.save(user);
		}
		repository.delete(cart);
	}

	@Transactional
	public void processOrder(Long orderId, Long userId, String address, String dateStr, String note) {
		// get the order
		Order order = repository.findById(orderId).orElseThrow();
		User user = userRepository.findById(userId).orElseThrow();

		// just checking the owner
		if (!order.getUser().getId().equals(userId)) {
			throw new RuntimeException("Acceso denegado");
		}

		// process the order
		order.setCompleted(true);
		order.setDeliveryAddress(address);
		order.setDeliveryNote(note);
		
		// the date
		if (dateStr != null && !dateStr.isEmpty()) {
			order.setDeliveryDate(LocalDate.parse(dateStr));
		} else {
			order.setDeliveryDate(LocalDate.now()); // by default
		}

		// just checking
		order.setUser(user);

		// add the order to the user's orders
		if (user.getOrders() == null) {
			user.setOrders(new ArrayList<>());
		}
		if (!user.getOrders().contains(order)) {
			user.getOrders().add(order);
		}

		// we save the order in the ddbb
		repository.saveAndFlush(order);

		// we change to a new cart
		user.setCart(null);
		userRepository.save(user);}
}