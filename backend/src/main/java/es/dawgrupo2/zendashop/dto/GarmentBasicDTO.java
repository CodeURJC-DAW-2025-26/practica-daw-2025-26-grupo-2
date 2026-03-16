package es.dawgrupo2.zendashop.dto;

public record GarmentBasicDTO (
    Long id,
    String name,
    Double price,
    ImageDTO image,
    String reference,
    String category,
    String description,
    String features
    // TODO: Ask about including folders for mappers and dtos, as there are many dtos and mappers, and it would be more organized
    // TODO: Implement DTOs for Statistics, ask about how could we do it, as we need to return some specific data for the statistics
) {
    
}