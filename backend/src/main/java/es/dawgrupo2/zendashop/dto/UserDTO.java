package es.dawgrupo2.zendashop.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Blob;

public record UserDTO(
    Long id,
    String name,
    String surname,
    String email,
    String adress,
    LocalDateTime creationDate,
    String encodedPassword,
    List<String> roles,
    OrderDTO cart,
    Blob avatar,
    List<OpinionDTO> opinions,
    List<OrderDTO> orders
) {
    
}
