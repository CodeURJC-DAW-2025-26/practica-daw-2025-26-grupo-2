import { redirect } from "react-router";

export class ApiError extends Error {
    status: number;
    constructor(status: number, message: string) {
        super(message);
        this.status = status;
    }
}

export async function apiFetch(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
    const res = await fetch(input, init);

    if (res.status === 401) {
        const from = window.location.pathname + window.location.search;
        throw redirect(`/login?from=${encodeURIComponent(from)}`);
    }

    if (res.status === 403) {
        throw redirect(`/error?message=${encodeURIComponent("Acceso no autorizado")}`);
    }

    if (!res.ok) {
        let message: string;
        try {
            const body = await res.json();
            message = body.error ?? body.message ?? `Error ${res.status}`;
        } catch {
            message = `Error ${res.status}`;
        }
        throw new ApiError(res.status, message);
    }

    return res;
}
