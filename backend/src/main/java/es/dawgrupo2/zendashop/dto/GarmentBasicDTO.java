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
) {
    
}