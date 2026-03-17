package es.dawgrupo2.zendashop.extendedDTO;

import es.dawgrupo2.zendashop.basicDTO.GarmentBasicDTO;
import es.dawgrupo2.zendashop.basicDTO.UserBasicDTO;

public record OpinionExtendedDTO(
    Long id,
    String comment,
    Integer rating,
    UserBasicDTO user,
    GarmentBasicDTO garment
) {
}
