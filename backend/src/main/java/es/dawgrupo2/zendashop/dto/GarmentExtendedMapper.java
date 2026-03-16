package es.dawgrupo2.zendashop.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.Garment;

@Mapper(componentModel = "spring")
public interface GarmentExtendedMapper {

    GarmentExtendedDTO toDTO(Garment garment);

    List<GarmentExtendedDTO> toDTOs(List<Garment> garments);

    @Mapping (target = "image", ignore = true)
    Garment toDomain(GarmentBasicDTO garmentDTO);


}
