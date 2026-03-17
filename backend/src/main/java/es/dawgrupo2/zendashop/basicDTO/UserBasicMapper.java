package es.dawgrupo2.zendashop.basicDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.User;

@Mapper(componentModel = "spring")
public interface UserBasicMapper {

    UserBasicDTO toDTO(User user);

    List<UserBasicDTO> toDTOs(List<User> users);

    @Mapping (target = "avatar", ignore = true)
    User toDomain(UserBasicDTO userDTO);


}