package es.dawgrupo2.zendashop.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.User;

@Mapper(componentModel = "spring")
public interface UserExtendedMapper {

    UserExtendedDTO toDTO(User user);

    List<UserExtendedDTO> toDTOs(List<User> users);

    @Mapping (target = "avatar", ignore = true)
    User toDomain(UserExtendedDTO userDTO);


}