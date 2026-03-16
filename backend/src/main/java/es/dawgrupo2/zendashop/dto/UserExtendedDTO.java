package es.dawgrupo2.zendashop.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserExtendedDTO(
    Long id,
    String name,
    String surname,
    String email,
    String address,
    LocalDateTime creationDate,
    List<String> roles,
    OrderBasicDTO cart,
    ImageDTO avatar,
    List<OpinionBasicDTO> opinions,
    List<OrderBasicDTO> orders
    //TODO: Ask if is necessary to include all this info in the DTO
) {
    
}
