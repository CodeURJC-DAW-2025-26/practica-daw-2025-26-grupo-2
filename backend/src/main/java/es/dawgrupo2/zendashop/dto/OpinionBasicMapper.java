package es.dawgrupo2.zendashop.dto;

import java.util.List;

import org.mapstruct.Mapper;


import es.dawgrupo2.zendashop.model.Opinion;

@Mapper(componentModel = "spring")
public interface OpinionBasicMapper {

    OpinionBasicDTO toDTO(Opinion opinion);

    List<OpinionBasicDTO> toDTOs(List<Opinion> opinions);


}
