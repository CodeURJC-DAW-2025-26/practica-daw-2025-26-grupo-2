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
    return res.json();
}

// GET /api/v1/users/{id} → datos de un usuario por id
export async function getUserById(id: number): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Usuario no encontrado");
    }
    return res.json();
}

export async function getCurrentUser(): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/me`);
    if (!res.ok) {
        throw new Error("No hay sesión iniciada");
    }
    return res.json();
}

export async function registerUser(
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
        throw new Error("Error al registrar el usuario");
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

export async function updateCurrentUser(
    name: string,
    surname: string,
    email: string,
    address: string,
    encodedPassword: string
): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/me`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, surname, email, address, encodedPassword }),
    });

    if (!res.ok) {
        throw new Error("Error al actualizar el perfil");
    }

    return res.json();
}

export async function deleteUser(id: number): Promise<UserExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al eliminar el usuario");
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

export async function getUserCart(id: number): Promise<OrderExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}/cart`);
    if (!res.ok) {
        throw new Error("Error al obtener el carrito del usuario");
    }
    return res.json();
}

export async function getCurrentUserCart(): Promise<OrderExtendedDTO> {
    const res = await fetch(`${API_URL}/me/cart`);
    if (!res.ok) {
        throw new Error("Error al obtener el carrito");
    }
    return res.json();
}

export async function getUserOrders(
    id: number,
    page: number,
    size: number
): Promise<OrderBasicDTO[]> {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const res = await fetch(`${API_URL}/${id}/orders?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Error al obtener los pedidos del usuario");
    }
    return res.json();
}

export async function getCurrentUserOrders(
    page: number,
    size: number
): Promise<OrderBasicDTO[]> {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const res = await fetch(`${API_URL}/me/orders?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Error al obtener tus pedidos");
    }
    return res.json();
}