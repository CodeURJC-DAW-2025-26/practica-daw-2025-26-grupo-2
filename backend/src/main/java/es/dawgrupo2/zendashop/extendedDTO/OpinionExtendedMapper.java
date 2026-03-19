package es.dawgrupo2.zendashop.extendedDTO;

import java.util.List;

import org.mapstruct.Mapper;

import es.dawgrupo2.zendashop.model.Opinion;

@Mapper(componentModel = "spring")
public interface OpinionExtendedMapper {

    OpinionExtendedDTO toDTO(Opinion opinion);

    List<OpinionExtendedDTO> toDTOs(List<Opinion> opinions);
}