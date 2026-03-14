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
    // TODO: Ask if is necessary to incude delete endpoint for Garment, as in our web we don't consider that option. That would change a lot of logic
    // Same for orders, cart, users...
    // TODO: Ask how to manage deletion of opinions when there are not yours
    List<OpinionBasicDTO> opinions
) {
}
