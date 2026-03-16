package es.dawgrupo2.zendashop.dto;

import java.util.List;

public record GarmentDTO (
    Long id,
    String name,
    Double price,
    ImageDTO image,
    String reference,
    String category,
    String description,
    String features,
    List<OpinionBasicDTO> opinions
) {
}
