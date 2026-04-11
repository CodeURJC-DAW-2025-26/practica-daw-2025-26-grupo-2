import type ImageDTO from "./ImageDTO";

export default interface UserBasicDTO {
  id: number;
  name: string;
  email: string;
  roles: string[];
  avatar: ImageDTO;
}
