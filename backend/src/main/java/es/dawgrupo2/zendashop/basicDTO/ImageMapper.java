package es.dawgrupo2.zendashop.basicDTO;

import org.mapstruct.Mapper;

import es.dawgrupo2.zendashop.model.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    
    ImageDTO toDTO(Image image);

}
