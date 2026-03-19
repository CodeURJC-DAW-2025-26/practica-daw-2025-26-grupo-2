package es.dawgrupo2.zendashop.basicDTO;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.dawgrupo2.zendashop.model.Order;

@Mapper(componentModel = "spring")
public interface OrderBasicMapper {

    OrderBasicDTO toDTO(Order order);

    List<OrderBasicDTO> toDTOs(List<Order> orders);

    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "shippingCost", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "user", ignore = true)
    Order toDomain(OrderBasicDTO orderDTO);

}
