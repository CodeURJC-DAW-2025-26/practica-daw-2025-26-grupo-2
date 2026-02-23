package es.dawgrupo2.zendashop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findDistinctByCompletedFalseAndOrderItems_Garment_Id(Long garmentId);
}