import type OpinionBasicDTO from "../dtos/OpinionBasicDTO";
import type OpinionExtendedDTO from "../dtos/OpinionExtendedDTO";


const API_URL = "/api/v1/garments";

export async function getOpinions(
    garmentId: number,
    page: number,
    size: number
): Promise<OpinionBasicDTO[]> {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const res = await fetch(`${API_URL}/${garmentId}/opinions/?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Error al obtener las opiniones");
    }
    return await res.json();
}

export async function getOpinion(garmentId: number, opinionId: number): Promise<OpinionExtendedDTO> {
    const res = await fetch(`${API_URL}/${garmentId}/opinions/${opinionId}`);
    if (!res.ok) {
        throw new Error("Qué buscabas? Opinión no encontrada");
    }
    return await res.json();
}


export async function addOpinion(
    garmentId: number,
    comment: string,
    rating: number
): Promise<OpinionExtendedDTO> {
    const res = await fetch(`${API_URL}/${garmentId}/opinions/`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({
            comment: comment, 
            rating: rating,  
        }),
    });

    if (!res.ok) {
        throw new Error("Error al añadir la opinión");
    }

    return await res.json();
}


export async function deleteOpinion(garmentId: number, opinionId: number): Promise<void> {
    const res = await fetch(`${API_URL}/${garmentId}/opinions/${opinionId}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al eliminar la opinión");
    }
}


export async function updateOpinion(
    garmentId: number,
    opinionId: number,
    comment: string,
    rating: number,
): Promise<OpinionExtendedDTO> {
    const res = await fetch(`${API_URL}/${garmentId}/opinions/${opinionId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({
            comment: comment, 
            rating: rating
        }),
    });

    if (!res.ok) {
        throw new Error("Error al actualizar la opinión");
    }

    return await res.json();
}

