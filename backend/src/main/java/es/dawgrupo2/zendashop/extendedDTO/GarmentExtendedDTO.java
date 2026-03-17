package es.dawgrupo2.zendashop.extendedDTO;

import java.util.List;

import es.dawgrupo2.zendashop.basicDTO.ImageDTO;
import es.dawgrupo2.zendashop.basicDTO.OpinionBasicDTO;

public record GarmentExtendedDTO (
    Long id,
    String name,
    Double price,
    ImageDTO image,
    String reference,
    String category,
    String description,
    String features,
    List<OpinionBasicDTO> opinions
) {
}
