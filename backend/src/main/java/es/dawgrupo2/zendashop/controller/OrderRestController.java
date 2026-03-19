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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.dawgrupo2.zendashop.basicDTO.OrderBasicDTO;
import es.dawgrupo2.zendashop.extendedDTO.OrderExtendedDTO;
import es.dawgrupo2.zendashop.basicDTO.OrderBasicMapper;
import es.dawgrupo2.zendashop.basicDTO.OrderItemBasicDTO;
import es.dawgrupo2.zendashop.basicDTO.OrderItemBasicMapper;
import es.dawgrupo2.zendashop.extendedDTO.OrderExtendedMapper;
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
@RequestMapping("/api/v1/orders")
public class OrderRestController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderBasicMapper orderBasicMapper;

	@Autowired
	private OrderItemBasicMapper orderItemBasicMapper;

	@Autowired
	private UserService userService;

    @Autowired
    private OrderExtendedMapper orderExtendedMapper;

	@GetMapping("/")
	public Page<OrderBasicDTO> getOrders(@RequestParam (required = false) Boolean completed, @PageableDefault(size = 10) Pageable pageable) {

		if (completed != null) {
            if (completed) {
                return orderService.findByCompletedTrue(pageable).map(orderBasicMapper::toDTO);
            } else {
                return orderService.findByCompletedFalse(pageable).map(orderBasicMapper::toDTO);
            }
		} else {
			return orderService.getOrders(pageable).map(orderBasicMapper::toDTO);
		}
	}

	@GetMapping("/{id}")
	public OrderExtendedDTO getOrder(@PageableDefault(size = 10) Pageable pageable, @PathVariable long id, HttpServletRequest request) {
		Order order = orderService.findById(id).orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));
		if (!request.isUserInRole("ADMIN") && !order.getUser().getEmail().equals(request.getUserPrincipal().getName())) {
			throw new AccessDeniedException("No tienes permiso para acceder a este pedido");
		}
		OrderExtendedDTO orderDTO = orderExtendedMapper.toDTO(order);
		return orderDTO;
	}

	@PostMapping("/")
	public ResponseEntity<OrderExtendedDTO> createOrder(@RequestBody OrderBasicDTO orderBasicDTO, HttpServletRequest request) {

		Order order = orderBasicMapper.toDomain(orderBasicDTO);
		String errorMsg = orderService.validateFields(order);
		if (!errorMsg.isEmpty()) {
			throw new IllegalArgumentException(errorMsg);
		}
		User user = userService.findByEmail(request.getUserPrincipal().getName()).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		order = orderService.createOrder(order, user);
		OrderExtendedDTO orderDTO = orderExtendedMapper.toDTO(order);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(orderDTO.id()).toUri();

		return ResponseEntity.created(location).body(orderDTO);
	}

	@PutMapping("/{id}")
	public OrderExtendedDTO replaceOrder(@PathVariable long id, @RequestBody OrderBasicDTO updatedOrderDTO) {

		Order updatedOrder = orderBasicMapper.toDomain(updatedOrderDTO);
		String errorMsg = orderService.validateFields(updatedOrder);
		if (!errorMsg.isEmpty()) {
			throw new IllegalArgumentException(errorMsg);
		}
		updatedOrder = orderService.updateOrder(id, updatedOrder);
		return orderExtendedMapper.toDTO(updatedOrder);
	}

	@DeleteMapping("/{id}")
	public OrderExtendedDTO deleteOrder(@PathVariable long id) {
        Order order = orderService.findById(id).orElseThrow();
        if (order.getCompleted()) {
            throw new IllegalStateException("Cannot delete an order that is completed");
        }
		return orderExtendedMapper.toDTO(orderService.delete(id));
	}
}