package es.dawgrupo2.zendashop.basicDTO;

public record OrderItemBasicDTO(
    Long id,
    GarmentBasicDTO garment,
    int quantity,
    String size
) {
}
