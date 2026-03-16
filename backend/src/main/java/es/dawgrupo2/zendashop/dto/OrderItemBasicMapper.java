package es.dawgrupo2.zendashop.dto;

import java.util.List;

import org.mapstruct.Mapper;


import es.dawgrupo2.zendashop.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemBasicMapper {

    OrderItemBasicDTO toDTO(OrderItem orderItem);

    List<OrderItemBasicDTO> toDTOs(List<OrderItem> orderItems);


}
