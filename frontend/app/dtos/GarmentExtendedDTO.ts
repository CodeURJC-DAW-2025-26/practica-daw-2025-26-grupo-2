import type ImageDTO from "./ImageDTO";

export default interface GarmentExtendedDTO {
  id: number;
  name: string;
  price: number;
  image: ImageDTO;
  reference: string;
  category: string;
  description: string;
  features: string;
}
