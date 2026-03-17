package es.dawgrupo2.zendashop.basicDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderBasicDTO(
    Long id,
    LocalDateTime orderDate,
    String status,
    BigDecimal totalPrice,
    UserBasicDTO user
) {
    
}
