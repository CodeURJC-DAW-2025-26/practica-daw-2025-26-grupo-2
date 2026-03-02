package es.dawgrupo2.zendashop.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.OrderItem;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.InvoicePdfService;
import es.dawgrupo2.zendashop.service.OrderService;
import es.dawgrupo2.zendashop.service.UserService;
import es.dawgrupo2.zendashop.service.OrderItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private InvoicePdfService invoicePdfService;

	@GetMapping("/orders") // FINISHED
	public String showOrders(Model model, @PageableDefault(size = 10) Pageable pageable) {
		model.addAttribute("orders", orderService.findByCompletedTrue(pageable));
		model.addAttribute("hasMore", orderService.findByCompletedTrue(pageable.next()).hasContent());
		return "all_orders";
	}

	@GetMapping("/loadMoreOrders")
	public String loadMoreOrders(Model model, @PageableDefault(size = 10) Pageable pageable,
			HttpServletResponse response) {
		model.addAttribute("orders", orderService.findByCompletedTrue(pageable));
		response.addHeader("X-Has-More", String.valueOf(orderService
				.findByCompletedTrue(pageable.next())
				.hasContent()));
		return "orders_cards";
	}

	@GetMapping("/order/{id}") // FINISHED
	public String showOrder(Model model, @PathVariable long id, HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable) {
		return showOrderDetail(model, id, request, false, pageable);
	}

	@GetMapping("/order/{id}/edit")
	public String editOrder(Model model, @PathVariable long id, HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable) {
		return showOrderDetail(model, id, request, true, pageable);
	}

	private String showOrderDetail(Model model, long id, HttpServletRequest request, boolean editing, Pageable pageable) {
		Optional<Order> op = orderService.findById(id);
		if (op.isPresent()) {
			Order edittingOrder = null;
			String backLink;
			Order order = op.get();
			Principal principal = request.getUserPrincipal();
			User user = userService.findByEmail(principal.getName()).orElseThrow();
			if (!request.isUserInRole("ADMIN") && !user.getEmail().equals(order.getUser().getEmail())) {
				model.addAttribute("message", "Quieto parao! No tienes permiso para ver este pedido.");
				model.addAttribute("backLink", "/myorders");
				return "customError";
			}
			String status;
			if (order.getCompleted()) {
				status = "COMPLETADO";
			} else {
				status = "EN CURSO";
			}
			model.addAttribute("order", order);
			model.addAttribute("orderItemsList", orderItemService.findByOrderId(order.getId(), pageable));
			model.addAttribute("hasMore", orderItemService.findByOrderId(order.getId(), pageable.next()).hasContent());
			model.addAttribute("status", status);
			if (editing){
				edittingOrder = order;
				backLink = "/orders";
			}
			else{
				if (request.isUserInRole("ADMIN")){
					if (order.getUser().getEmail().equals(user.getEmail())){
						backLink = "/myorders";
					}
					else{
						backLink = "/orders";
					}
				}
				else{
					backLink = "/myorders";
				}
			}
			model.addAttribute("backLink", backLink);
			model.addAttribute("edditingOrder", edittingOrder);
			model.addAttribute("tableFormat", true);
			return "order_detail";
		} else {
			model.addAttribute("message", "¿Qué buscabas? Pedido no encontrado");
			model.addAttribute("backLink", "/");
			return "customError";
		}
	}
	

	@GetMapping("/cart") // FINISHED
	public String showCart(Model model, HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable) {
		Principal principal = request.getUserPrincipal();
		User user = userService.findByEmail(principal.getName()).orElseThrow();
		model.addAttribute("order", user.getCart());
		if (user.getCart() != null) {
			model.addAttribute("orderItemsList", orderItemService.findByOrderId(user.getCart().getId(), pageable));
			model.addAttribute("hasMore", orderItemService
					.findByOrderId(user.getCart().getId(), pageable.next())
					.hasContent());
		} else {
			model.addAttribute("hasMore", false);
		}
		return "cart";
	}

	@GetMapping("/myorders") // FINISHED
	public String showUserOrders(Model model, HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable) {
		Principal principal = request.getUserPrincipal();
		Optional<User> opUser = userService.findByEmail(principal.getName());
		model.addAttribute("orders", orderService.findByUserIdAndCompletedTrue(opUser.get().getId(), pageable));
		model.addAttribute("hasMore", orderService.findByUserIdAndCompletedTrue(opUser.get().getId(), pageable.next()).hasContent());
		return "user_orders";
	}

	@GetMapping("/loadMoreMyOrders")
	public String loadMoreMyOrders(Model model, @PageableDefault(size = 10) Pageable pageable,
			HttpServletResponse response, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		User user = userService.findByEmail(principal.getName()).orElseThrow();
		model.addAttribute("orders", orderService.findByUserIdAndCompletedTrue(user.getId(), pageable));
		response.addHeader("X-Has-More", String.valueOf(orderService
				.findByUserIdAndCompletedTrue(user.getId(), pageable.next())
				.hasContent()));
		return "user_orders_cards";
	}

	@PostMapping("/cart/add/{garmentId}") // FINISHED
	public String addToCart(Model model, @PathVariable long garmentId, OrderItem orderItem, HttpServletRequest request) {
		Optional<Garment> opGarment = garmentService.findById(garmentId);
		Principal principal = request.getUserPrincipal();
		User user = userService.findByEmail(principal.getName()).orElseThrow();
		Order cart = user.getCart();
		if (!opGarment.isPresent()) {
			model.addAttribute("message", "¿Qué buscabas? La prenda que has intentado añadir no existe.");
			model.addAttribute("backLink", "/");
			return "customError";
		}
		orderItem.setGarment(opGarment.get());
		if (cart != null) {
			cart.addOrderItem(orderItem);
			orderService.save(cart); // OrderItems are saved by cascade, but we need to save the cart to update the
										// relationship
		} else {
			Order newOrder = new Order(false, null, null, null);
			orderService.save(newOrder); // We need to save the order first to generate an ID for the relationship
			newOrder.setUser(user);
			newOrder.addOrderItem(orderItem);
			user.setCart(newOrder);
			userService.save(user);
		}
		return "redirect:/";
	}

	@PostMapping("/cart/remove/{orderItemId}") // FINISHED
	public String removeFromCart(Model model, @PathVariable long orderItemId, Principal principal) {
		Optional<OrderItem> opOrderItem = orderItemService.findById(orderItemId);
		if (!opOrderItem.isPresent()) {
			model.addAttribute("message", "¿Qué buscabas? No se ha encontrado el artículo que has intentado eliminar del carrito.");
			model.addAttribute("backLink", "/cart");
			return "customError";
		}
		User user = userService.findByEmail(principal.getName()).orElseThrow();
		Order cart = user.getCart();
		if (cart != null) {
			cart.removeOrderItem(opOrderItem.get());
			if (cart.getOrderItems().isEmpty()) {
				user.setCart(null);
				userService.save(user);
				orderService.delete(cart.getId());
				return "redirect:/";
			}
			orderItemService.delete(orderItemId); // Not necessary if orphanRemoval is set, but it ensures the order item is deleted
			userService.save(user);
			return "redirect:/cart";
		}
		return "redirect:/";
	}

	@GetMapping("/orders/{id}/invoice")
	public Object generateInvoice(@PathVariable Long id, Model model, HttpServletRequest request) {
		Optional<Order> opOrder = orderService.findById(id);
		if(!opOrder.isPresent()) {
			model.addAttribute("message", "¿Qué buscabas? Pedido no encontrado.");
			model.addAttribute("backLink", "/orders");
			return "customError";
		}
		Order order = opOrder.get();
		if (!request.isUserInRole("ADMIN") && !request.getUserPrincipal().getName().equals(order.getUser().getEmail())) {
			model.addAttribute("message", "Quieto parao! No tienes permiso para ver esta factura.");
			model.addAttribute("backLink", "/myorders");
			return "customError";
		}
		byte[] pdf = invoicePdfService.generateInvoice(order);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(
				ContentDisposition.inline().filename("factura-pedido-" + id + ".pdf").build());
		return ResponseEntity.ok()
				.headers(headers)
				.body(pdf);
	}

	@PostMapping("/order/{id}/process")
	public String processOrder(
			@PathVariable Long id,
			@RequestParam String deliveryAddress,
			@RequestParam String deliveryDate,
			@RequestParam String deliveryNote,
			HttpServletRequest request, Model model) {
		
		Optional<Order> opOrder = orderService.findById(id);
		if(!opOrder.isPresent()) {
			model.addAttribute("message", "¿Qué buscabas? Pedido no encontrado.");
			model.addAttribute("backLink", "/orders");
			return "customError";
		}
		Order order = opOrder.get();
		if (!request.isUserInRole("ADMIN") && !request.getUserPrincipal().getName().equals(order.getUser().getEmail())) {
			model.addAttribute("message", "Quieto parao! No tienes permiso para procesar este pedido");
			model.addAttribute("backLink", "/");
			return "customError";
		}
		// get the user
		String email = request.getUserPrincipal().getName();
		User user = userService.findByEmail(email).orElseThrow();

		// process shopping
		orderService.processOrder(id, user.getId(), deliveryAddress, deliveryDate, deliveryNote);

		return "redirect:/myorders";
	}

@PostMapping("/order/{id}/delete")
	public String deleteOrder(Model model, @PathVariable long id, HttpServletRequest request) {
	Optional<Order> opOrder = orderService.findById(id);
	if(!opOrder.isPresent()) {
		model.addAttribute("message", "¿Qué buscabas? Pedido no encontrado.");
		model.addAttribute("backLink", "/orders");
		return "customError";
	}
	Order order = opOrder.get();
	if (!request.isUserInRole("ADMIN")) {
		model.addAttribute("message", "Quieto parao! No tienes permiso para eliminar este pedido");
		model.addAttribute("backLink", "/");
		return "customError";
	}
	User user = order.getUser();
	if (!order.getCompleted()){
		user.setCart(null);
	}else {
		user.removeOrder(order);
	}
	userService.save(user);
	orderService.delete(id);
	return "redirect:/orders";
}
	
	@PostMapping("/order/{id}/edit")
	public String updateOrderDetails(@PathVariable Long id,
			@RequestParam BigDecimal totalPrice,
			@RequestParam BigDecimal shippingCost,
			@RequestParam String deliveryAddress,
			@RequestParam LocalDate deliveryDate,
			@RequestParam String deliveryNote,
			HttpServletRequest request, Model model) {

		
		//Not neessary to check if user has permission, because only admins can access to this url
		Optional<Order> op = orderService.findById(id);

		if (op.isPresent()) {
			Order originalOrder = op.get();

			orderService.validateFields(shippingCost, totalPrice);

			originalOrder.setShippingCost(shippingCost);
			originalOrder.setTotalPrice(totalPrice);
			originalOrder.setDeliveryAddress(deliveryAddress);
			originalOrder.setDeliveryDate(deliveryDate);
			originalOrder.setDeliveryNote(deliveryNote);

			orderService.save(originalOrder);

			return "redirect:/order/" + id;
		} else {
			model.addAttribute("message", "¿Qué buscabas? Pedido no encontrado.");
			model.addAttribute("backLink", "/orders");
			return "customError";
		}
	}

	private String loadMoreOrderItems(Long orderId, Model model, Pageable pageable,
			HttpServletResponse response, boolean tableFormat) {
		model.addAttribute("orderItemsList", orderItemService.findByOrderId(orderId, pageable));
		response.addHeader("X-Has-More", String.valueOf(orderItemService
				.findByOrderId(orderId, pageable.next())
				.hasContent()));
		model.addAttribute("tableFormat", tableFormat);
		return "order_items_cards";
	}

	@GetMapping("/loadMoreOrderItemsCart")
	public String loadMoreOrderItemsCart(@RequestParam Long orderId, Model model, @PageableDefault(size = 10) Pageable pageable,
			HttpServletResponse response) {
		return loadMoreOrderItems(orderId, model, pageable, response, false);
	}

	@GetMapping("/loadMoreOrderItemsOrder")
	public String loadMoreOrderItemsOrder(@RequestParam Long orderId, Model model, @PageableDefault(size = 10) Pageable pageable,
			HttpServletResponse response) {
		return loadMoreOrderItems(orderId, model, pageable, response, true);
	}

}