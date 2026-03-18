package es.dawgrupo2.zendashop.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;

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

import es.dawgrupo2.zendashop.basicDTO.OrderBasicDTO;
import es.dawgrupo2.zendashop.extendedDTO.OrderExtendedDTO;
import es.dawgrupo2.zendashop.basicDTO.OrderBasicMapper;
import es.dawgrupo2.zendashop.extendedDTO.OrderExtendedMapper;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.service.OrderService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderBasicMapper orderBasicMapper;

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
	public OrderExtendedDTO getOrder(@PathVariable long id) {

		return orderExtendedMapper.toDTO(orderService.findById(id).orElseThrow());
	}

	/*@PostMapping("/")
	public ResponseEntity<OrderExtendedDTO> createOrder(@RequestBody OrderExtendedDTO orderDTO) {

		Order order = orderExtendedMapper.toDomain(orderDTO);
		order = orderService.createOrder(order);
		orderDTO = orderExtendedMapper.toDTO(order);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(orderDTO.id()).toUri();

		return ResponseEntity.created(location).body(orderDTO);
	}

	@PutMapping("/{id}")
	public OrderExtendedDTO replaceOrder(@PathVariable long id, @RequestBody OrderExtendedDTO updatedOrderDTO) throws SQLException {

		Order updatedOrder = orderExtendedMapper.toDomain(updatedOrderDTO);
		updatedOrder = orderService.replaceOrder(id, updatedOrder);
		return orderExtendedMapper.toDTO(updatedOrder);
	}*/

	@DeleteMapping("/{id}")
	public OrderExtendedDTO deleteOrder(@PathVariable long id) {
        Order order = orderService.findById(id).orElseThrow();
        if (order.getCompleted()) {
            throw new IllegalStateException("Cannot delete an order that is completed");
        }
		return orderExtendedMapper.toDTO(orderService.delete(id));
	}
}