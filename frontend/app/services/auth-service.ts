import { redirect } from "react-router";
import { useUserStore } from "~/stores/user-store";
import type UserExtendedDTO from "~/dtos/UserExtendedDTO";

export async function requireAuth(request: Request): Promise<UserExtendedDTO> {
    await useUserStore.getState().loadLoggedUser();
    const { user } = useUserStore.getState();
    if (!user) {
        const url = new URL(request.url);
        throw redirect(`/login?from=${encodeURIComponent(url.pathname + url.search)}`);
    }
    return user;
}

export function requireRole(user: UserExtendedDTO, role: string): void {
    if (!user.roles?.includes(role)) {
        throw redirect(`/error?message=${encodeURIComponent("Acceso no autorizado")}`);
    }
}

export function requireOwnerOrRole(user: UserExtendedDTO, ownerId: number, role: string): void {
    if (!user.roles?.includes(role) && user.id !== ownerId) {
        throw redirect(`/error?message=${encodeURIComponent("Acceso no autorizado")}`);
    }
}

