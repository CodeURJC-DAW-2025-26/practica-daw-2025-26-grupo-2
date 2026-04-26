import { create } from "zustand";

import type UserExtendedDTO from "~/dtos/UserExtendedDTO";
import { HttpError, logIn, logOut, reqIsLogged } from "~/services/login-service";

interface UserState {
  user: UserExtendedDTO | null;
  loginError: string | null;
  loadLoggedUser: () => Promise<void>;
  loginUser: (username: string, password: string) => Promise<void>;
  logoutUser: () => Promise<void>;
}

export const useUserStore = create<UserState>((set, get) => ({
  user: null,
  loginError: null,

  loadLoggedUser: async () => {
    try {
      const user = await reqIsLogged();
      set({ user, loginError: null });
    } catch (error) {
      if (error instanceof HttpError && error.status === 401) {
        set({ user: null, loginError: null });
        return;
      }

      console.log(error);
      set({ loginError: "Error al cargar el usuario conectado" });
    }
  },

  loginUser: async (username: string, password: string) => {
    set({ user: null, loginError: null });

    try {
      await logIn(username, password);
      await get().loadLoggedUser();
    } catch (error) {
      console.log(error);
      const message = "Nombre de usuario o contraseña incorrectos. Por favor, inténtelo de nuevo.";
      set({ loginError: message });
    }
  },

  logoutUser: async () => {
    set({ user: null, loginError: null });

    try {
      await logOut();
    } catch (error) {
      console.log(error);
      set({ loginError: "Logout fallido. Por favor, inténtelo de nuevo." });
    }
  },
}));