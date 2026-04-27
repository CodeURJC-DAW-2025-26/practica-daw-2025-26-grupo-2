import { apiFetch } from "./api-fetch";
import type UserBasicDTO from "../dtos/UserBasicDTO";
import type UserExtendedDTO from "../dtos/UserExtendedDTO";

const API_URL = "/api/v1/users";
const API_IMAGES_URL = "/api/v1/images";

export async function getUsers(
  page: number,
  size: number
): Promise<UserBasicDTO[]> {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
  });

  const res = await apiFetch(`${API_URL}/?${params.toString()}`);

  if (!res.ok) {
    throw new Error("Error al obtener los usuarios");
  }

  const data = await res.json();
  return Array.isArray(data) ? data : data.content ?? [];
}

export async function getUser(id: number): Promise<UserExtendedDTO> {
  const res = await apiFetch(`${API_URL}/${id}`);

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
  const res = await apiFetch(`${API_URL}/`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name,
      surname,
      email,
      address,
      encodedPassword,
    }),
  });

  if (!res.ok) {
    const message = await res.text();
    throw new Error(message || "Error al añadir el usuario");
  }

  return res.json();
}

export async function getUserOrderStats(userId: number) {
    
  const countRes = await apiFetch(
    `/api/v1/users/${userId}/orders?size=1`,
    { credentials: "include" }
  );
  if (!countRes.ok) throw new Error("Error al obtener pedidos del usuario");
  const countJson = await countRes.json();

  const now = new Date();
  const currentMonth = now.getMonth();
  const currentYear = now.getFullYear();

  const ordersRes = await apiFetch(
    `/api/v1/users/${userId}/orders?size=100`,
    { credentials: "include" }
  );
  if (!ordersRes.ok) throw new Error("Error al obtener pedidos del usuario");
  const ordersJson = await ordersRes.json();

  const allOrders = ordersJson.content ?? [];

  const thisMonthOrders = allOrders.filter((o: any) => {
    const d = new Date(o.creationDate);
    return d.getMonth() === currentMonth && d.getFullYear() === currentYear;
  });

  const orderCount = thisMonthOrders.length;

  const averageTicketLastMonth =
    orderCount > 0
      ? thisMonthOrders.reduce((sum: number, o: any) => sum + (o.totalPrice ?? 0), 0) / orderCount
      : 0;

  return { averageTicketLastMonth, orderCount };
}

export async function getUserMeanTicketChart(userId: number, period: "month" | "year") {
  const number = period === "month" ? 12 : 5;
  const [dataRes, labelsRes] = await Promise.all([
    apiFetch(`/api/v1/statistics/users/${userId}?period=${period}&number=${number}`, {
      credentials: "include",
    }),
    apiFetch(`/api/v1/statistics/labels?period=${period}&number=${number}`, {
      credentials: "include",
    }),
  ]);

  if (!dataRes.ok || !labelsRes.ok) throw new Error("Error al obtener la gráfica");

  const [dataJson, labelsJson] = await Promise.all([dataRes.json(), labelsRes.json()]);

  return {
    data: dataJson.data as number[],
    labels: labelsJson.data as string[],
  };
}

export async function updateUser(
  id: number,
  name: string,
  surname: string,
  email: string,
  address: string,
  encodedPassword: string
): Promise<UserExtendedDTO> {
  const res = await apiFetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name,
      surname,
      email,
      address,
      encodedPassword,
    }),
  });

  if (!res.ok) {
    throw new Error("Error al actualizar el usuario");
  }

  return res.json();
}

export async function disableUser(id: number): Promise<UserExtendedDTO> {
  const res = await apiFetch(`${API_URL}/${id}`, {
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

  const res = await apiFetch(`${API_URL}/${id}/images/`, {
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
  const res = await apiFetch(
    `${API_URL}/${userId}/images/${avatarId}`,
    {
      method: "DELETE",
    }
  );

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

  const res = await apiFetch(`${API_IMAGES_URL}/${imageId}/media`, {
    method: "PUT",
    body: formData,
  });

  if (!res.ok) {
    throw new Error("Error al reemplazar el avatar del usuario");
  }

}