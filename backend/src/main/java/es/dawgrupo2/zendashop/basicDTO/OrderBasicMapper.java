package es.dawgrupo2.zendashop.basicDTO;

import java.util.List;

import org.mapstruct.Mapper;


import es.dawgrupo2.zendashop.model.Order;

@Mapper(componentModel = "spring")
public interface OrderBasicMapper {

    OrderBasicDTO toDTO(Order order);

    List<OrderBasicDTO> toDTOs(List<Order> orders);


}
