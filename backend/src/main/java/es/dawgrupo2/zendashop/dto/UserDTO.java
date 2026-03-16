package es.dawgrupo2.zendashop.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Blob;

public record UserDTO(
    Long id,
    String name,
    String surname,
    String email,
    String address,
    LocalDateTime creationDate,
    List<String> roles,
    OrderDTO cart,
    Blob avatar,
    List<OpinionBasicDTO> opinions,
    List<OrderDTO> orders
) {
    
}
