package es.dawgrupo2.zendashop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.OrderItem;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.repository.OrderRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private UserService userService;

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

	public List<Order> findAll() {
		return repository.findAll();
	}

	public void save(Order order) {
		repository.save(order);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	public List<Order> findByUserId(Long userId) {
		return repository.findByUserId(userId);
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
		for (OrderItem item : cart.getOrderItems()) {
			orderItemService.delete(item.getId());
		}
		User user = cart.getUser();
		if (user != null) {
			user.setCart(null);
			cart.setUser(null);
			userService.save(user);
		}
		repository.delete(cart);
	}
}