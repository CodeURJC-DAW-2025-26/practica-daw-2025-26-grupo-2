package es.dawgrupo2.zendashop.basicDTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemBasicMapper {

    OrderItemBasicDTO toDTO(OrderItem orderItem);

    List<OrderItemBasicDTO> toDTOs(List<OrderItem> orderItems);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    OrderItem toDomain(OrderItemBasicDTO orderItemBasicDTO);

}
