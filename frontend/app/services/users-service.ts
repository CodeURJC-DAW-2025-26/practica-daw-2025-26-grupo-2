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
  const ordersRes = await apiFetch(
    `/api/v1/users/${userId}/orders?size=100&page=0`
  );
  if (!ordersRes.ok) throw new Error("Error al obtener pedidos del usuario");
  const ordersJson = await ordersRes.json();

  const allOrders = ordersJson.content ?? (Array.isArray(ordersJson) ? ordersJson : []);

  const now = new Date();
  const currentMonth = now.getMonth();
  const currentYear = now.getFullYear();

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

// Generates labels locally when /statistics/labels returns 403 for normal users
function generateLocalLabels(period: "month" | "year", number: number): string[] {
  const now = new Date();
  const labels: string[] = [];

  if (period === "month") {
    const monthNames = ["ene", "feb", "mar", "abr", "may", "jun",
                        "jul", "ago", "sep", "oct", "nov", "dic"];
    for (let i = number - 1; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      labels.push(`${monthNames[d.getMonth()]} ${String(d.getFullYear()).slice(2)}`);
    }
  } else {
    for (let i = number - 1; i >= 0; i--) {
      labels.push(String(now.getFullYear() - i));
    }
  }

  return labels;
}

export async function getUserMeanTicketChart(userId: number, period: "month" | "year") {
  const number = period === "month" ? 12 : 5;

  // apiFetch handles auth (JWT) and throws on 401/403 — safe to use here
  const dataRes = await apiFetch(
    `/api/v1/statistics/users/${userId}?period=${period}&number=${number}`
  );
  const dataJson = await dataRes.json();

  // Use raw fetch for labels — apiFetch would throw a redirect on 403,
  // preventing us from falling back to locally generated labels
  let labels: string[];
  try {
    const labelsRes = await fetch(
      `/api/v1/statistics/labels?period=${period}&number=${number}`
    );
    if (labelsRes.ok) {
      const labelsJson = await labelsRes.json();
      labels = labelsJson.data as string[];
    } else {
      labels = generateLocalLabels(period, number);
    }
  } catch {
    labels = generateLocalLabels(period, number);
  }

  return {
    data: dataJson.data as number[],
    labels,
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