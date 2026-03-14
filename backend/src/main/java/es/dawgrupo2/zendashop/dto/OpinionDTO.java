package es.dawgrupo2.zendashop.dto;

public record OpinionDTO(
    Long id,
    String comment,
    Integer rating,
    UserBasicDTO user,
    GarmentBasicDTO garment
) {
}
