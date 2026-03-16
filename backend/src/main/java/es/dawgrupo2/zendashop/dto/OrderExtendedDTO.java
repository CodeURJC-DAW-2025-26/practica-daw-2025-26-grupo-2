package es.dawgrupo2.zendashop.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderExtendedDTO(
    Long id,
    LocalDateTime orderDate,
    String status,
    BigDecimal totalPrice,
    BigDecimal shippingCost,
    BigDecimal subtotal,
    UserBasicDTO user,
    List<OrderItemBasicDTO> orderItems,
    String deliveryAddress,
    LocalDate deliveryDate
) {
    
}