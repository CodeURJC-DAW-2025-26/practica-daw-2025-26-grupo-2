package es.dawgrupo2.zendashop.extendedDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.User;

@Mapper(componentModel = "spring")
public interface UserExtendedMapper {

    @Mapping(target = "encodedPassword", ignore = true)
    UserExtendedDTO toDTO(User user);

    List<UserExtendedDTO> toDTOs(List<User> users);

    @Mapping (target = "avatar", ignore = true)
    @Mapping(target = "opinions", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "disabled", ignore = true)
    User toDomain(UserExtendedDTO userDTO);


}