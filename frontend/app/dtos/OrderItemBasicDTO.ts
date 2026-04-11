import type GarmentBasicDTO from "./GarmentBasicDTO";

export default interface OrderItemBasicDTO {
  id: number;
  garment: GarmentBasicDTO;
  quantity: number;
  size: string;
}
