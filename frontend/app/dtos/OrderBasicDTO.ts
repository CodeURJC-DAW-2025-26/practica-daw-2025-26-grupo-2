import type UserBasicDTO from "./UserBasicDTO";

export default interface OrderBasicDTO {
  id: number;
  creationDate: string;
  completed: boolean;
  totalPrice: number;
  user: UserBasicDTO;
  deliveryAddress: string;
  deliveryNote: string;
  deliveryDate: string;
}
