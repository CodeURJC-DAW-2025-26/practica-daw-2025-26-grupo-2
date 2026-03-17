package es.dawgrupo2.zendashop.extendedDTO;

import java.time.LocalDateTime;
import java.util.List;

import es.dawgrupo2.zendashop.basicDTO.ImageDTO;
import es.dawgrupo2.zendashop.basicDTO.OpinionBasicDTO;
import es.dawgrupo2.zendashop.basicDTO.OrderBasicDTO;

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
) {
    
}
