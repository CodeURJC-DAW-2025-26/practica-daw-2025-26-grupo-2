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
    @Mapping(target = "surname", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "encodedPassword", ignore = true)
    @Mapping(target = "opinions", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "disabled", ignore = true)
    User toDomain(UserBasicDTO userDTO);
}