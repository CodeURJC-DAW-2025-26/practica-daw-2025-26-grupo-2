package es.dawgrupo2.zendashop.basicDTO;

public record GarmentBasicDTO (
    Long id,
    String name,
    Double price,
    ImageDTO image,
    String reference,
    String category,
    String description,
    String features
    // TODO: Implement DTOs for Statistics, ask about how could we do it, as we need to return some specific data for the statistics
) {
    
}