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
import org.springframework.web.bind.annotation.ModelAttribute;
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

import org.springframework.web.bind.annotation.RequestBody;
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
	public String loadMoreGarments(Model model, @PageableDefault(size = 10) Pageable pageable,
			HttpServletResponse response) {
		model.addAttribute("orders", orderService.findByCompletedTrue(pageable));
		response.addHeader("X-Has-More", String.valueOf(orderService
				.findByCompletedTrue(pageable.next())
				.hasContent()));
		return "orders_cards";
	}

	@GetMapping("/order/{id}") // FINISHED
	public String getMethodName(Model model, @PathVariable long id, Principal principal), HttpServletRequest request) {
		Optional<Order> op = orderService.findById(id);
		if (op.isPresent()) {
			Order order = op.get();
			User user = userService.findByEmail(principal.getName()).orElseThrow();
			if (!request.isUserInRole("ADMIN") && !user.getName().equals(order.getUser().getEmail())) {
				model.addAttribute("message", "Quieto parao! No tienes permiso para ver este pedido.");
				model.addAttribute("backLink", "/myorders");
				return "customError";
			}
			String status;
			if (order.getCompleted()) {
				status = "COMPLETADO";
				model.addAttribute("isCompleted", true);
			} else {
				status = "EN CURSO";
				model.addAttribute("isPending", true);
			}
			model.addAttribute("order", order);
			model.addAttribute("status", status);
			model.addAttribute("backLink", "/orders");
			return "order_detail";
		} else {
			model.addAttribute("message", "¿Qué buscabas? Pedido no encontrado");
			model.addAttribute("backLink", "/");
			return "customError";
		}
	}

	@GetMapping("/cart") // FINISHED
	public String showCart(Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName()).orElseThrow();
		model.addAttribute("order", user.getCart());
		return "cart";
	}

	@GetMapping("/myorders") // FINISHED
	public String showUserOrders(Model model, Principal principal) {
		Optional<User> opUser = userService.findByEmail(principal.getName());
		model.addAttribute("orders", orderService.findByUserIdAndCompletedTrue(opUser.get().getId()));
		return "user_orders";
	}

	@PostMapping("/cart/add/{garmentId}") // FINISHED
	public String addToCart(Model model, @PathVariable long garmentId, Principal principal, OrderItem orderItem) {
		Optional<Garment> opGarment = garmentService.findById(garmentId);
		if (!opGarment.isPresent()) {
			model.addAttribute("element", "Prenda");
			model.addAttribute("masculine", false);
			return "garment_not_found";
		}
		orderItem.setGarment(opGarment.get());
		User user = userService.findByEmail(principal.getName()).orElseThrow();
		Order cart = user.getCart();
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
			model.addAttribute("element", "Prenda");
			model.addAttribute("masculine", false);
			return "not_found";
		}
		User user = userService.findByEmail(principal.getName()).orElseThrow();
		Order cart = user.getCart();
		if (cart != null) {
			cart.removeOrderItem(opOrderItem.get());
			if (cart.getOrderItems().isEmpty()) {
				user.setCart(null);
				userService.save(user);
				orderService.delete(cart.getId());
			}
			orderItemService.delete(orderItemId); // Not necessary if orphanRemoval is set, but it ensures the order
													// item is deleted
			userService.save(user);
		}
		return "redirect:/";
	}

	@GetMapping("/orders/{id}/invoice")
	public ResponseEntity<byte[]> generateInvoice(@PathVariable Long id) {
		Order order = orderService.findById(id).orElseThrow();
		// TODO: Add security check to ensure only the user who made the order or an
		// admin can access the invoice
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
			HttpServletRequest request) {

		// get the user
		String email = request.getUserPrincipal().getName();
		User user = userService.findByEmail(email).orElseThrow();

		// process shopping
		orderService.processOrder(id, user.getId(), deliveryAddress, deliveryDate, deliveryNote);

		return "redirect:/myorders";
	}

	@PostMapping("/orders/{id}/update")
	public String updateOrderDetails(@PathVariable Long id,
			@RequestParam BigDecimal totalPrice,
			@RequestParam BigDecimal shippingCost,
			@RequestParam String status,
			@RequestParam String deliveryAddress,
			@RequestParam LocalDate deliveryDate,
			@RequestParam String deliveryNote,
			HttpServletRequest request) {

		Optional<Order> op = orderService.findById(id);

		if (op.isPresent()) {
			Order originalOrder = op.get();

			if (!request.isUserInRole("ADMIN")) {
				return "redirect:/access-denied";
			}

			if (totalPrice != null) {
				originalOrder.setTotalPrice(totalPrice);
			}

			if (shippingCost != null) {
				originalOrder.setShippingCost(shippingCost);
			}

			if (status != null) {
				originalOrder.setCompleted("COMPLETED".equals(status));
			}

			if (deliveryAddress != null) {
				originalOrder.setDeliveryAddress(deliveryAddress);
			}

			if (deliveryDate != null) {
				originalOrder.setDeliveryDate(deliveryDate);
			}

			if (deliveryNote != null) {
				originalOrder.setDeliveryNote(deliveryNote);
			}

			orderService.save(originalOrder);

			return "redirect:/order/" + id;
		} else {
			return "not_found";
		}
	}

}