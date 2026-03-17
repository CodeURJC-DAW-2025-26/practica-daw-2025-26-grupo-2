package es.dawgrupo2.zendashop.extendedDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import es.dawgrupo2.zendashop.basicDTO.OrderItemBasicDTO;
import es.dawgrupo2.zendashop.basicDTO.UserBasicDTO;

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