package es.dawgrupo2.zendashop.extendedDTO;

import es.dawgrupo2.zendashop.basicDTO.GarmentBasicDTO;
import es.dawgrupo2.zendashop.basicDTO.OrderBasicDTO;

public record OrderItemExtendedDTO (
    Long id,
    GarmentBasicDTO garment,
    int quantity,
    String size,
    OrderBasicDTO order
){
    
}