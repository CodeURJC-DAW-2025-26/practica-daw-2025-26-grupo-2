package es.dawgrupo2.zendashop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.dawgrupo2.zendashop.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}