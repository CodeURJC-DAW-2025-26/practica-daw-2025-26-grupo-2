import type UserBasicDTO from "./UserBasicDTO";

export default interface OrderExtendedDTO {
  id: number;
  creationDate: string;
  completed: boolean;
  totalPrice: number;
  shippingCost: number;
  subtotal: number;
  user: UserBasicDTO;
  deliveryAddress: string;
  deliveryDate: string;
  deliveryNote: string;
}
