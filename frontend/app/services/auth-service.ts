import { redirect } from "react-router";
import { useUserStore } from "~/stores/user-store";

export function requireAuth() {
    const { user } = useUserStore.getState();
    if (!user) {
      throw redirect("/login");
    }
}

export function requireRole(role: string) {
    const { user } = useUserStore.getState();
    if (!(user?.roles.includes(role))) {
      throw redirect("/unauthorized");
      //TODO: Create unauthorized page
    }
}
