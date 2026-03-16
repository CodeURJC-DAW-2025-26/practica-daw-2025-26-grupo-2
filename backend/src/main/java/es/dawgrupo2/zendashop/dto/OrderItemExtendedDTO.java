package es.dawgrupo2.zendashop.dto;

public record OrderItemExtendedDTO (
    Long id,
    GarmentBasicDTO garment,
    int quantity,
    String size,
    OrderBasicDTO order
){
    
}