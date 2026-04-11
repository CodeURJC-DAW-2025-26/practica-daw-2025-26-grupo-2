import type GarmentBasicDTO from "./GarmentBasicDTO";
import type OrderBasicDTO from "./OrderBasicDTO";

export default interface OrderItemExtendedDTO {
  id: number;
  garment: GarmentBasicDTO;
  quantity: number;
  size: string;
  order: OrderBasicDTO;
}
