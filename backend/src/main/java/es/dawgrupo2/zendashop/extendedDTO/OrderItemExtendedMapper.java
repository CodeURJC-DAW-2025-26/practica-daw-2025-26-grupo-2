package es.dawgrupo2.zendashop.extendedDTO;

import java.util.List;

import org.mapstruct.Mapper;

import es.dawgrupo2.zendashop.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemExtendedMapper {

    OrderItemExtendedDTO toDTO(OrderItem orderItem);

    List<OrderItemExtendedDTO> toDTOs(List<OrderItem> orderItems);
}