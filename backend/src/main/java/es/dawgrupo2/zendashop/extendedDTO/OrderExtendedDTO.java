package es.dawgrupo2.zendashop.extendedDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import es.dawgrupo2.zendashop.basicDTO.UserBasicDTO;

public record OrderExtendedDTO(
    Long id,
    LocalDateTime creationDate,
    Boolean completed,
    BigDecimal totalPrice,
    BigDecimal shippingCost,
    BigDecimal subtotal,
    UserBasicDTO user,
    String deliveryAddress,
    LocalDate deliveryDate,
    String deliveryNote
) {
    
}