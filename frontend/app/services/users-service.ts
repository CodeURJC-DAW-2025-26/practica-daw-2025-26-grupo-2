import type UserBasicDTO from "../dtos/UserBasicDTO";
import type UserExtendedDTO from "../dtos/UserExtendedDTO";
import type OrderBasicDTO from "../dtos/OrderBasicDTO";
import type OrderExtendedDTO from "../dtos/OrderExtendedDTO";

const API_URL = "/api/v1/users";
const API_IMAGES_URL = "/api/v1/images";

export async function getUsers(
    page: number,
    size: number
): Promise<UserBasicDTO[]> {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const res = await fetch(`${API_URL}/?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Error al obtener los usuarios");
    }
    const data = await res.json();
    return Array.isArray(data) ? data : data.content ?? [];
}

export async function getUser(id: number): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Qué buscabas? Usuario no encontrado");
    }
    return res.json();
}

export async function addUser(
    name: string,
    surname: string,
    email: string,
    address: string,
    encodedPassword: string
): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, surname, email, address, encodedPassword }),
    });

    if (!res.ok) {
        throw new Error("Error al añadir el usuario");
    }

    return res.json();
}

export async function updateUser(
    id: number,
    name: string,
    surname: string,
    email: string,
    address: string,
    encodedPassword: string
): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, surname, email, address, encodedPassword }),
    });

    if (!res.ok) {
        throw new Error("Error al actualizar el usuario");
    }

    return res.json();
}

export async function disableUser(id: number): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al deshabilitar el usuario");
    }

    return res.json();
}

export async function uploadUserImage(
    id: number,
    imageFile: File
): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);

    const res = await fetch(`${API_URL}/${id}/images/`, {
        method: "POST",
        body: formData,
    });

    if (!res.ok) {
        throw new Error("Error al subir el avatar del usuario");
    }
}

export async function deleteUserImage(
    userId: number,
    avatarId: number
): Promise<void> {
    const res = await fetch(`${API_URL}/${userId}/images/${avatarId}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al eliminar el avatar del usuario");
    }
}

export async function replaceUserImage(
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
        throw new Error("Error al reemplazar el avatar del usuario");
    }
}