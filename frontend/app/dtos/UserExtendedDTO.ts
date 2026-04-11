import type ImageDTO from "./ImageDTO";
import type OrderBasicDTO from "./OrderBasicDTO";

export default interface UserExtendedDTO {
  id: number;
  name: string;
  surname: string;
  email: string;
  address: string;
  creationDate: string; 
  encodedPassword: string;
  roles: string[];
  cart: OrderBasicDTO;
  avatar: ImageDTO;
}
