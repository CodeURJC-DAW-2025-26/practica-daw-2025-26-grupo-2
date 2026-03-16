package es.dawgrupo2.zendashop.dto;

public record OpinionExtendedDTO(
    Long id,
    String comment,
    Integer rating,
    UserBasicDTO user,
    GarmentBasicDTO garment
) {
}
