package es.dawgrupo2.zendashop.extendedDTO;

import java.util.List;

import org.mapstruct.Mapper;


import es.dawgrupo2.zendashop.model.Order;

@Mapper(componentModel = "spring")
public interface OrderExtendedMapper {

    OrderExtendedDTO toDTO(Order order);

    List<OrderExtendedDTO> toDTOs(List<Order> orders);
}

