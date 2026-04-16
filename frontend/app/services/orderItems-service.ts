import type OrderItemBasicDTO from "../dtos/OrderItemBasicDTO";
import type OrderItemExtendedDTO from "../dtos/OrderItemExtendedDTO";

const API_URL = "/api/v1/orders";

export async function getOrderItems(
    orderId: number,
    page: number,
    size: number
): Promise<OrderItemBasicDTO[]> {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    const res = await fetch(`${API_URL}/${orderId}/orderItems/?${params.toString()}`);
    if (!res.ok) {
        throw new Error("Error al obtener los elementos del pedido");
    }
    return await res.json();
}

export async function getOrderItem(orderId: number, id: number): Promise<OrderItemExtendedDTO> {
    const res = await fetch(`${API_URL}/${orderId}/orderItems/${id}`);
    if (!res.ok) {
        throw new Error("Qué buscabas? Elemento del pedido no encontrado");
    }
    return await res.json();
}


export async function addOrderItem(
    orderId: number,
    garmentId: number,
    quantity: number,
    size: string
): Promise<OrderItemExtendedDTO> {
    const res = await fetch(`${API_URL}/${orderId}/orderItems/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            garment: { id: garmentId },  // ← OrderItemBasicDTO tiene garment: GarmentBasicDTO, no garmentId
            quantity: quantity,
            size: size,
        }),
    });

    if (!res.ok) {
        throw new Error("Error al añadir el elemento del pedido");
    }

    return await res.json();
}

export async function disableOrderItem(orderId: number, id: number): Promise<void> {
    const res = await fetch(`${API_URL}/${orderId}/orderItems/${id}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        throw new Error("Error al eliminar el elemento del pedido");
    }
}


export async function updateOrderItem(
    orderId: number,
    id: number,
    garmentId: number,
    quantity: number,   
    size: string,
): Promise<OrderItemExtendedDTO> {
    const res = await fetch(`${API_URL}/${orderId}/orderItems/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            garment: { id: garmentId },  // ← igual que el POST
            quantity: quantity,
            size: size,
        }),
    });

    if (!res.ok) {
        throw new Error("Error al actualizar el elemento del pedido");
    }

    return await res.json();
}

