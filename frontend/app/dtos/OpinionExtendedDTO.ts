import type GarmentBasicDTO from "./GarmentBasicDTO";
import type UserBasicDTO from "./UserBasicDTO";

export default interface OpinionExtendedDTO {
  id: number;
  comment: string;
  rating: number;
  user: UserBasicDTO;
  garment: GarmentBasicDTO;
}
