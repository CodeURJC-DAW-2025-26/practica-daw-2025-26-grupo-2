package es.dawgrupo2.zendashop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.dawgrupo2.zendashop.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
        long deleteByOrderCompletedFalseAndGarment_Id(Long garmentId);
        Page<OrderItem> findByOrder_Id(Long orderId, Pageable pageable);

}