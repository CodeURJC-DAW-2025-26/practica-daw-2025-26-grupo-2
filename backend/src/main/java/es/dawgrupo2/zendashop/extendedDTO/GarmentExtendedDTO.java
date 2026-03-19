package es.dawgrupo2.zendashop.extendedDTO;

import java.math.BigDecimal;

import es.dawgrupo2.zendashop.basicDTO.ImageDTO;

public record GarmentExtendedDTO (
    Long id,
    String name,
    BigDecimal price,
    ImageDTO image,
    String reference,
    String category,
    String description,
    String features
    //List<OpinionBasicDTO> opinions
) {
}
