package es.dawgrupo2.zendashop.extendedDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import es.dawgrupo2.zendashop.basicDTO.OrderItemBasicDTO;
import es.dawgrupo2.zendashop.basicDTO.UserBasicDTO;

public record OrderExtendedDTO(
    Long id,
    LocalDateTime creationDate,
    Boolean completed,
    BigDecimal totalPrice,
    BigDecimal shippingCost,
    BigDecimal subtotal,
    UserBasicDTO user,
    Page<OrderItemBasicDTO> orderItems,
    String deliveryAddress,
    LocalDate deliveryDate
) {
    
}