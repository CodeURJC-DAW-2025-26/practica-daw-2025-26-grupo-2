package es.dawgrupo2.zendashop.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;

import es.dawgrupo2.zendashop.basicDTO.OrderItemBasicDTO;
import es.dawgrupo2.zendashop.extendedDTO.OrderExtendedDTO;
import es.dawgrupo2.zendashop.extendedDTO.OrderItemExtendedDTO;
import es.dawgrupo2.zendashop.basicDTO.OrderItemBasicMapper;
import es.dawgrupo2.zendashop.extendedDTO.OrderItemExtendedMapper;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.OrderItem;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.OrderItemService;
import es.dawgrupo2.zendashop.service.OrderService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/orderItems")
public class OrderItemRestController {

	@Autowired
	private OrderItemService orderItemService;

    @Autowired
    private OrderService orderService;
	
    @Autowired
    private UserService userService;

	@Autowired
	private OrderItemBasicMapper orderItemBasicMapper;

    @Autowired
    private OrderItemExtendedMapper orderItemExtendedMapper;

	@GetMapping("/")
	public Page<OrderItemBasicDTO> getOrderItems(@PathVariable long orderId, @RequestParam (required = false) Boolean completed, @PageableDefault(size = 10) Pageable pageable) {
        return orderItemService.findByOrderId(orderId, pageable).map(orderItemBasicMapper::toDTO);
	}

	@GetMapping("/{id}")
	public OrderItemExtendedDTO getOrderItem(@PathVariable long orderId, @PathVariable long id, HttpServletRequest request) throws AccessDeniedException {
		Order order = orderService.findById(orderId).orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));
		OrderItem orderItem = orderItemService.findById(id).orElseThrow(() -> new NoSuchElementException("Artículo de pedido no encontrado"));
		if (!request.isUserInRole("ADMIN") && !order.getUser().getEmail().equals(request.getUserPrincipal().getName())) {
			throw new AccessDeniedException("No tienes permiso para acceder a los artículos de este pedido");
		}
		if (orderItem.getOrder().getId() != orderId) {
            throw new NoSuchElementException("El artículo de pedido no pertenece al pedido especificado");
        }
		return orderItemExtendedMapper.toDTO(orderItem);
	}

	@PostMapping("/")
	public ResponseEntity<OrderItemExtendedDTO> createOrderItem(@PathVariable long orderId, @RequestBody OrderItemBasicDTO orderItemBasicDTO, HttpServletRequest request) throws AccessDeniedException {
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow();
		Order order = orderService.findById(orderId).orElseThrow();
		if (!request.isUserInRole("ADMIN") && !order.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("No tienes permiso para añadir artículos a este pedido");
		}
		if (order.getCompleted()) {
			throw new IllegalStateException("No se pueden añadir artículos a un pedido completado");
		}
		OrderItem orderItem = orderItemBasicMapper.toDomain(orderItemBasicDTO);
		String errorMsg = orderItemService.validateFields(orderItem);
		if (errorMsg != null) {
			throw new IllegalArgumentException(errorMsg);
		}
		orderItem = orderItemService.createOrderItem(order, orderItem);
		OrderItemExtendedDTO orderItemDTO = orderItemExtendedMapper.toDTO(orderItem);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(orderItemDTO.id()).toUri();

		return ResponseEntity.created(location).body(orderItemDTO);
	}

	@PutMapping("/{id}")
	public OrderItemExtendedDTO replaceOrderItem(@PathVariable long id, @RequestBody OrderItemBasicDTO updatedOrderItemDTO, HttpServletRequest request, @PathVariable long orderId) throws SQLException {
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		Order order = orderService.findById(orderId).orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));
		if (!request.isUserInRole("ADMIN") && !order.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("No tienes permiso para editar artículos de este pedido");
		}
		if (order.getCompleted()) {
			throw new IllegalStateException("No se pueden editar artículos de un pedido completado");
		}
		OrderItem originalOrderItem = orderItemService.findById(id).orElseThrow(() -> new NoSuchElementException("Artículo de pedido no encontrado"));
		if (originalOrderItem.getOrder().getId() != orderId) {
			throw new NoSuchElementException("El artículo de pedido no pertenece al pedido especificado");
		}
		OrderItem updatedOrderItem = orderItemBasicMapper.toDomain(updatedOrderItemDTO);
		String errorMsg = orderItemService.validateFields(updatedOrderItem);
		if (errorMsg != null && !errorMsg.isEmpty()) {
			throw new IllegalArgumentException(errorMsg);
		}
		originalOrderItem = orderItemService.updateOrderItem(originalOrderItem, updatedOrderItem);
		OrderItemExtendedDTO orderItemExtendedDTO = orderItemExtendedMapper.toDTO(originalOrderItem);
		return orderItemExtendedDTO;
		}

	@DeleteMapping("/{id}")
	public OrderItemExtendedDTO deleteOrderItem(@PathVariable long orderId, @PathVariable long id, HttpServletRequest request) throws AccessDeniedException {
        Order order = orderService.findById(orderId).orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		if (!request.isUserInRole("ADMIN") && !order.getUser().getId().equals(user.getId())) {
			throw new AccessDeniedException("No tienes permiso para eliminar artículos de este pedido");
		}
		if (order.getCompleted()) {
			throw new IllegalStateException("No se pueden eliminar artículos de un pedido completado");
		}
		OrderItem orderItem = orderItemService.findById(id).orElseThrow(() -> new NoSuchElementException("Artículo de pedido no encontrado"));
		if (orderItem.getOrder().getId() != orderId) {
			throw new NoSuchElementException("El artículo de pedido no pertenece al pedido especificado");
		}
		return orderItemExtendedMapper.toDTO(orderItemService.delete(id));
	}
}