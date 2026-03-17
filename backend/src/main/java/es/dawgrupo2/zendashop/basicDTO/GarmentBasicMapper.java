package es.dawgrupo2.zendashop.basicDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.Garment;

@Mapper(componentModel = "spring")
public interface GarmentBasicMapper {

    GarmentBasicDTO toDTO(Garment garment);

    List<GarmentBasicDTO> toDTOs(List<Garment> garments);

    @Mapping (target = "image", ignore = true)
    Garment toDomain(GarmentBasicDTO garmentDTO);


}
