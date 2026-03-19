package es.dawgrupo2.zendashop.basicDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.Opinion;

@Mapper(componentModel = "spring")
public interface OpinionBasicMapper {

    OpinionBasicDTO toDTO(Opinion opinion);

    List<OpinionBasicDTO> toDTOs(List<Opinion> opinions);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "garment", ignore = true)
    @Mapping(target = "own", ignore = true)
    Opinion toDomain(OpinionBasicDTO opinionBasicDTO);

}
