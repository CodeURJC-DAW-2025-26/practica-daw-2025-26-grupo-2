import type GarmentBasicDTO from "../dtos/GarmentBasicDTO";
import type GarmentExtendedDTO from "../dtos/GarmentExtendedDTO";

const API_URL = "/api/v1/garments";
const API_IMAGES_URL = "/api/v1/images";

export async function getGarments(
    nameSearch : string, 
    categorySearch: string, 
    minPrice: number, 
    maxPrice: number, 
    sort: string, 
    page: number, 
    size: number
): Promise<GarmentBasicDTO[]> {
    const params = new URLSearchParams({nameSearch, categorySearch, minPrice: minPrice.toString(), maxPrice: maxPrice.toString(), page: page.toString(), size: size.toString(), sort});
    const res = await fetch(`${API_URL}?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Error al obtener las prendas");
    }
    return res.json();
}


export async function getGarment(id: number): Promise<GarmentExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Qué buscabas? Prenda no encontrada");
    }
    return res.json();
}


export async function addGarment(
    name: string,
    price: number,
    category: string,
    description: string,
    features: string,
): Promise<GarmentExtendedDTO> {
    const res = await fetch(`${API_URL}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({
            name: name, 
            price: price, 
            category: category, 
            description: description, 
            features: features
        }),
    });

    if (!res.ok) {
        throw new Error("Error al añadir la prenda");
    }

    return await res.json();
}


export async function disableGarment(id: number): Promise<void> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al deshabilitar la prenda");
    }
}


export async function updateGarment(
    id: number,
    name: string,
    price: number,
    category: string,
    description: string,
    features: string
): Promise<GarmentExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({
            name: name, 
            price: price, 
            category: category, 
            description: description, 
            features: features
        }),
    });

    if (!res.ok) {
        throw new Error("Error al actualizar la prenda");
    }

    return await res.json();
}


export async function uploadGarmentImage(
    id: number, imageFile: File
): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);

    const res = await fetch(`${API_URL}/${id}/images`, {
        method: "POST",
        body: formData,
    });

    if (!res.ok) {
        throw new Error("Error al subir la imagen de la prenda");
    }
}


export async function deleteGarmentImage(
    garmentId: number, imageId: number
): Promise<void> {
    const res = await fetch(`${API_URL}/${garmentId}/images/${imageId}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al eliminar la imagen de la prenda");
    }
}


export async function replaceGarmentImage(
    imageId: number, 
    imageFile: File
): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);

    const res = await fetch(`${API_IMAGES_URL}/${imageId}/media`, {
        method: "PUT",
        body: formData,
    });

    if (!res.ok) {
        throw new Error("Error al reemplazar la imagen de la prenda");
    }
}