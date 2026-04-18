import { Link } from "react-router";
import type GarmentBasicDTO from "../dtos/GarmentBasicDTO";

interface Props {
  garment: GarmentBasicDTO;
}

export default function GarmentCard({ garment }: Props) {
    // We build the URL for the image
    const imageUrl = garment.image 
        ? `/api/v1/images/${garment.image.id}/media` 
        : "/placeholder-garment.jpg";

    return (
        <>
        <Link to={`/garment/${garment.id}`}>
            <img 
            src={imageUrl} 
            className="img-fluid" 
            alt={garment.name}
            loading="lazy" 
            />
        </Link>
        <h4 className="mt-2 h6">{garment.name}</h4>
        <h5 className="fw-bold">{garment.price}€</h5>
        </>
    );
}