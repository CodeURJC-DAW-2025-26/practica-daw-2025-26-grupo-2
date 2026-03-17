package es.dawgrupo2.zendashop.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public Page<Order> findByCompletedTrue(Pageable pageable) {
		return repository.findByCompletedTrue(pageable);
	}

	public void save(Order order) {
		repository.save(order);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	// For admin purposes, to set the creation date to a specific value (to initialize the database with old orders, for example)
	public void forceCreationDate(Long id, LocalDateTime date) {
		repository.forceCreationDate(id, date);
	}

	public Page<Order> findByUserIdAndCompletedTrue(Long userId, Pageable pageable) {
		return repository.findByUserIdAndCompletedTrue(userId, pageable);
	}

	public List<Order> findByCompletedFalseAndOrderItems_Garment_Id(long id) {
		return repository.findDistinctByCompletedFalseAndOrderItems_Garment_Id(id);
	}

	public Double sumIncomeBetween(LocalDateTime start, LocalDateTime end) {
		return repository.sumIncomeBetween(start, end);
	}

	public long countOrdersBetween(LocalDateTime start, LocalDateTime end) {
		return repository.countOrdersBetween(start, end);
	}

	public Double sumIncomeByMonth(int month, int year) {
		return repository.sumIncomeByMonth(month, year);
	}

	public long countByMonth(int month, int year) {
		return repository.countOrdersByMonth(month, year);
	}

	public Double sumIncomeByYear(int year) {
		return repository.sumIncomeByYear(year);
	}

	public List<String> getDailyLabelsLastDays(int days) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
		List<String> labels = new ArrayList<>();
		for (int offset = days - 1; offset >= 0; offset--) {
			labels.add(LocalDate.now().minusDays(offset).format(formatter));
		}
		return labels;
	}

	public List<Double> getDailyIncomeLastDays(int days) {
		Map<LocalDate, Double> incomeByDay = new LinkedHashMap<>();
		for (int offset = days - 1; offset >= 0; offset--) {
			incomeByDay.put(LocalDate.now().minusDays(offset), 0.0);
		}

		LocalDate firstDay = LocalDate.now().minusDays(days - 1);
		LocalDateTime start = firstDay.atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetween(start, end);

		for (Order order : orders) {
			LocalDate day = order.getCreationDate().toLocalDate();
			double current = incomeByDay.getOrDefault(day, 0.0);
			double orderTotal = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0;
			incomeByDay.put(day, current + orderTotal);
		}

		return new ArrayList<>(incomeByDay.values());
	}

	public List<Long> getDailyOrdersLastDays(int days) {
		Map<LocalDate, Long> ordersByDay = new LinkedHashMap<>();
		for (int offset = days - 1; offset >= 0; offset--) {
			ordersByDay.put(LocalDate.now().minusDays(offset), 0L);
		}

		LocalDate firstDay = LocalDate.now().minusDays(days - 1);
		LocalDateTime start = firstDay.atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetween(start, end);

		for (Order order : orders) {
			LocalDate day = order.getCreationDate().toLocalDate();
			ordersByDay.put(day, ordersByDay.getOrDefault(day, 0L) + 1);
		}

		return new ArrayList<>(ordersByDay.values());
	}

	public List<String> getMonthlyLabelsLastMonths(int months) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.of("es", "ES"));
		List<String> labels = new ArrayList<>();
		for (int offset = months - 1; offset >= 0; offset--) {
			labels.add(YearMonth.now().minusMonths(offset).format(formatter));
		}
		return labels;
	}

	public List<Double> getMonthlyIncomeLastMonths(int months) {
		Map<YearMonth, Double> incomeByMonth = new LinkedHashMap<>();
		for (int offset = months - 1; offset >= 0; offset--) {
			incomeByMonth.put(YearMonth.now().minusMonths(offset), 0.0);
		}

		YearMonth firstMonth = YearMonth.now().minusMonths(months - 1);
		LocalDateTime start = firstMonth.atDay(1).atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetween(start, end);

		for (Order order : orders) {
			YearMonth month = YearMonth.from(order.getCreationDate());
			double current = incomeByMonth.getOrDefault(month, 0.0);
			double orderTotal = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0;
			incomeByMonth.put(month, current + orderTotal);
		}

		return new ArrayList<>(incomeByMonth.values());
	}

	public List<Long> getMonthlyOrdersLastMonths(int months) {
		Map<YearMonth, Long> ordersByMonth = new LinkedHashMap<>();
		for (int offset = months - 1; offset >= 0; offset--) {
			ordersByMonth.put(YearMonth.now().minusMonths(offset), 0L);
		}

		YearMonth firstMonth = YearMonth.now().minusMonths(months - 1);
		LocalDateTime start = firstMonth.atDay(1).atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetween(start, end);

		for (Order order : orders) {
			YearMonth month = YearMonth.from(order.getCreationDate());
			ordersByMonth.put(month, ordersByMonth.getOrDefault(month, 0L) + 1);
		}

		return new ArrayList<>(ordersByMonth.values());
	}

	public List<String> getYearlyLabelsLastYears(int years) {
		List<String> labels = new ArrayList<>();
		for (int offset = years - 1; offset >= 0; offset--) {
			labels.add(String.valueOf(LocalDate.now().minusYears(offset).getYear()));
		}
		return labels;
	}

	public List<Double> getYearlyIncomeLastYears(int years) {
		Map<Integer, Double> incomeByYear = new LinkedHashMap<>();
		for (int offset = years - 1; offset >= 0; offset--) {
			incomeByYear.put(LocalDate.now().minusYears(offset).getYear(), 0.0);
		}

		int firstYear = LocalDate.now().minusYears(years - 1).getYear();
		LocalDateTime start = LocalDate.of(firstYear, 1, 1).atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetween(start, end);

		for (Order order : orders) {
			int year = order.getCreationDate().getYear();
			double current = incomeByYear.getOrDefault(year, 0.0);
			double orderTotal = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0;
			incomeByYear.put(year, current + orderTotal);
		}

		return new ArrayList<>(incomeByYear.values());
	}

	public List<Long> getYearlyOrdersLastYears(int years) {
		Map<Integer, Long> ordersByYear = new LinkedHashMap<>();
		for (int offset = years - 1; offset >= 0; offset--) {
			ordersByYear.put(LocalDate.now().minusYears(offset).getYear(), 0L);
		}

		int firstYear = LocalDate.now().minusYears(years - 1).getYear();
		LocalDateTime start = LocalDate.of(firstYear, 1, 1).atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetween(start, end);

		for (Order order : orders) {
			int year = order.getCreationDate().getYear();
			ordersByYear.put(year, ordersByYear.getOrDefault(year, 0L) + 1);
		}

		return new ArrayList<>(ordersByYear.values());
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
		
		user.addOrder(order);

		// we save the order in the ddbb
		repository.save(order);

		// we change to a new cart
		user.setCart(null);
		userRepository.save(user);
	}

	public List<Double> getMonthlyMeanTicketLastMonthsById(int months, Long userId) {
		Map<YearMonth, Double> totalAmountByMonth = new LinkedHashMap<>();
		Map<YearMonth, Integer> countByMonth = new LinkedHashMap<>();

		for (int offset = months - 1; offset >= 0; offset--) {
			YearMonth ym = YearMonth.now().minusMonths(offset);
			totalAmountByMonth.put(ym, 0.0);
			countByMonth.put(ym, 0);
		}

		YearMonth firstMonth = YearMonth.now().minusMonths(months - 1);
		LocalDateTime start = firstMonth.atDay(1).atStartOfDay();
		LocalDateTime end = LocalDateTime.now();

		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetweenAndUser_Id(start, end, userId);

		for (Order order : orders) {
			YearMonth month = YearMonth.from(order.getCreationDate());
			if (totalAmountByMonth.containsKey(month)) {
				double orderTotal = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0;
				totalAmountByMonth.put(month, totalAmountByMonth.get(month) + orderTotal);
				countByMonth.put(month, countByMonth.get(month) + 1);
			}
		}

		List<Double> meanTickets = new ArrayList<>();
		totalAmountByMonth.forEach((month, total) -> {
			Integer count = countByMonth.get(month);
			double average = (count > 0) ? (total / count) : 0.0;
			meanTickets.add(average);
		});

		return meanTickets;
	}

	public List<Double> getYearlyMeanTicketLastYearsById(int years, Long userId) {
		Map<Integer, Double> totalAmountByYear = new LinkedHashMap<>();
		Map<Integer, Integer> countByYear = new LinkedHashMap<>();
		for (int offset = years - 1; offset >= 0; offset--) {
			int year = LocalDate.now().minusYears(offset).getYear();
			totalAmountByYear.put(year, 0.0);
			countByYear.put(year, 0);
		}

		int firstYear = LocalDate.now().minusYears(years - 1).getYear();
		LocalDateTime start = LocalDate.of(firstYear, 1, 1).atStartOfDay();
		LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
		List<Order> orders = repository.findByCompletedTrueAndCreationDateBetweenAndUser_Id(start, end, userId);

		for (Order order : orders) {
			int year = order.getCreationDate().getYear();
			if (totalAmountByYear.containsKey(year)) {
				double orderTotal = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0;
				totalAmountByYear.put(year, totalAmountByYear.get(year) + orderTotal);
				countByYear.put(year, countByYear.get(year) + 1);
			}
		}

		List<Double> meanTickets = new ArrayList<>();
		totalAmountByYear.forEach((year, total) -> {
			Integer count = countByYear.get(year);
			double average = (count > 0) ? (total / count) : 0.0;
			meanTickets.add(average);
		});

		return meanTickets;
	}

	public String validateFields(BigDecimal shippingCost, BigDecimal totalPrice) {
		String errorMsg = "";
		if (shippingCost == null || shippingCost.compareTo(BigDecimal.ZERO) < 0 || shippingCost.compareTo(new BigDecimal("1000000")) > 0) {
			errorMsg += "El coste de envío no puede ser negativo ni superar 1.000.000.";
		}
		if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) < 0 || totalPrice.compareTo(new BigDecimal("1000000")) > 0) {
			errorMsg += "El precio total no puede ser negativo ni superar 1.000.000.";
		}
		return errorMsg;
	}

	public void newCart(OrderItem orderItem, User user) {
		Order newOrder = new Order(false, null, null, null);
		save(newOrder); // We need to save the order first to generate an ID for the relationship
		newOrder.setUser(user);
		newOrder.addOrderItem(orderItem);
		user.setCart(newOrder);
		userRepository.save(user);
	}

	//TODO: In order rest controller, only allow to delete carts, not orders
	//TODO: In order item rest controller, only allow to delete, add and edit order items from carts, not from completed orders
	//TODO: In order rest controller, only allow to update carts, not completed orders. This update should only allow to to change delivery date, delivery address and delivery note, not the order item. For changing the order items, we should use the order item rest controller.
	
}