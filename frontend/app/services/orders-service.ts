import type OrderBasicDTO from "../dtos/OrderBasicDTO";
import type OrderExtendedDTO from "../dtos/OrderExtendedDTO";

const API_URL = "/api/v1/orders";

export async function getOrders(page: number): Promise<OrderBasicDTO[]> {
    const params = new URLSearchParams({ page: page.toString() });
    // TODO: Implement pagination
    const res = await fetch(`${API_URL}?${params.toString()}`);
    return res.json();
}


export async function getOrder(id: number): Promise<OrderExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) {
        throw new Error("Qué buscabas? Pedido no encontrado");
    }
    return res.json();
}


export async function addOrder(
    deliveryAddress: string,
    deliveryNote: string,
    deliveryDate: string
): Promise<OrderExtendedDTO> {
    const res = await fetch(`${API_URL}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({
            deliveryAddress: deliveryAddress, 
            deliveryNote: deliveryNote, 
            deliveryDate: deliveryDate
        }),
    });

    if (!res.ok) {
        throw new Error("Error al añadir el pedido");
    }

    return await res.json();
}


export async function deleteOrder(id: number): Promise<void> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al eliminar el pedido");
    }
}


export async function updateOrder(
    id: number,
    deliveryAddress: string,
    deliveryNote: string,
    deliveryDate: string,
    completed: boolean
): Promise<OrderExtendedDTO> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({
            deliveryAddress: deliveryAddress, 
            deliveryNote: deliveryNote, 
            deliveryDate: deliveryDate,
            completed: completed
        }),
    });

    if (!res.ok) {
        throw new Error("Error al actualizar el pedido");
    }

    return await res.json();
}