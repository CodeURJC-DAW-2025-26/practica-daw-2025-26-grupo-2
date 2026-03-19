package es.dawgrupo2.zendashop.basicDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderBasicDTO(
    Long id,
    LocalDateTime creationDate,
    Boolean completed,
    BigDecimal totalPrice,
    UserBasicDTO user,
    String deliveryAddress,
    String deliveryNote,
    LocalDate deliveryDate
) {
    
}
