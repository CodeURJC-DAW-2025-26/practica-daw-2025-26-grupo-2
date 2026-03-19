package es.dawgrupo2.zendashop.basicDTO;

import java.math.BigDecimal;

public record GarmentBasicDTO (
    Long id,
    String name,
    BigDecimal price,
    ImageDTO image,
    String reference,
    String category,
    String description,
    String features
    // TODO: Implement DTOs for Statistics, ask about how could we do it, as we need to return some specific data for the statistics
) {
    
}