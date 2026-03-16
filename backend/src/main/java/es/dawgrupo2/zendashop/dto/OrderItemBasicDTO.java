package es.dawgrupo2.zendashop.dto;

public record OrderItemBasicDTO(
    Long id,
    GarmentBasicDTO garment,
    int quantity,
    String size
) {
}
