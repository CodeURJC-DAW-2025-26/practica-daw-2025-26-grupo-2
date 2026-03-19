package es.dawgrupo2.zendashop.extendedDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//import es.dawgrupo2.zendashop.basicDTO.GarmentBasicDTO;
import es.dawgrupo2.zendashop.model.Garment;

@Mapper(componentModel = "spring")
public interface GarmentExtendedMapper {

    GarmentExtendedDTO toDTO(Garment garment);

    List<GarmentExtendedDTO> toDTOs(List<Garment> garments);

    @Mapping (target = "image", ignore = true)
    @Mapping (target = "opinions", ignore = true)
    @Mapping (target = "orderItems", ignore = true)
    @Mapping (target = "available", ignore = true)
    Garment toDomain(GarmentExtendedDTO garmentDTO);


}
