import type ImageDTO from "./ImageDTO";

export default interface GarmentBasicDTO {
  id: number;
  name: string;
  price: number;
  image: ImageDTO | null;
  reference: string;
  category: string;
  description: string;
  features: string;
}
