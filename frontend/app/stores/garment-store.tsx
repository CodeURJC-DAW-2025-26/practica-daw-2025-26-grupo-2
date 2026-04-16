import { create } from "zustand";

import type GarmentBasicDTO from "../dtos/GarmentBasicDTO";
import type GarmentExtendedDTO from "~/dtos/GarmentExtendedDTO";
import * as garmentService from "../services/garments-service";

interface GarmentsState {
  garments: GarmentBasicDTO[];
  selectedGarment: GarmentExtendedDTO | null;
  error: string | null;

  loadGarments: (params: {
        nameSearch: string;
        categorySearch: string;
        minPrice: number;
        maxPrice: number;
        sort: string;
        page: number;
        size: number;
    }) => Promise<void>;
  loadGarmentById: (id: number) => Promise<void>;

  createGarment: (data: {
        name: string;
        price: number;
        category: string;
        description: string;
        features: string;
  }) => Promise<void>;

  removeGarment: (id: number) => Promise<void>;

  updateGarment: (id: number, data: any) => Promise<void>;

  uploadImage: (id: number, file: File) => Promise<void>;

  deleteImage: (garmentId: number, imageId: number) => Promise<void>;

  replaceImage: (garmentId: number, imageId: number, file: File) => Promise<void>;
}

export const useUserStore = create<GarmentsState>((set, get) => ({
  garments: [],
  selectedGarment: null,
  error: null,

  //cuando se implemente el componente .tsx habrá que hacer useEffect(() => { loadGarments() }, []); para cargar las prendas
  loadGarments: async (params) => {
    set({ error: null });

    try {
      const data = await garmentService.getGarments(
        params.nameSearch, params.categorySearch, params.minPrice, 
        params.maxPrice, params.sort, params.page, params.size
      );
      set({ garments: data });
    } catch (err) {
      console.error("Error loading garments:", err);
      set({ error: "Error loading garments" });
    }
  },

  loadGarmentById: async (id) => {
        set({ error: null });
        try {
            const garment = await garmentService.getGarment(id);
            set({ selectedGarment: garment });
        } catch (err) {
          console.error("Error loading garment específico:", err);
          set({ error: "Error loading garment específico" });
        }
    },

    createGarment: async (data) => {
        try {
            const newGarment = await garmentService.addGarment(
                data.name, data.price, data.category, data.description, data.features
            );
            // Updating the list of garments
            set((state) => ({ 
                garments: [...state.garments, newGarment as unknown as GarmentBasicDTO]
            }));
        } catch (err) {
          console.error("Error creando un garment:", err);
          set({ error: "Error creando un garment" });
        }
    },

    removeGarment: async (id) => {
        try {
            await garmentService.disableGarment(id);
            set((state) => ({
                garments: state.garments.filter(g => (g as any).id !== id),
                selectedGarment: state.selectedGarment?.id === id ? null : state.selectedGarment
            }));
        } catch (err) {
          console.error("Error borrando un garment:", err);
          set({ error: "Error borrando un garment" });
        }
    },

    updateGarment: async (id, data) => {
        try {
            const updated = await garmentService.updateGarment(
                id, data.name, data.price, data.category, data.description, data.features
            );
            set((state) => ({
                garments: state.garments.map(g => g.id === id ? updated : g),
                selectedGarment: updated
            }));
        } catch (err) {
          console.error("Error actualizando un garment:", err);
          set({ error: "Error actualizando un garment" });
        }
    },

    uploadImage: async (id, file) => {
        try {
            await garmentService.uploadGarmentImage(id, file);

            get().loadGarmentById(id);
        } catch (err) {
          console.error("Error subiendo la imagen del garment:", err);
          set({ error: "Error subiendo la imagen del garment" });
        }
    },

    deleteImage: async (garmentId: number, imageId: number) => {
        try {
            await garmentService.deleteGarmentImage(garmentId, imageId);
            
            const currentGarment = get().selectedGarment;
            
            // If the selected garment is the one we are editing
            if (currentGarment && currentGarment.id === garmentId) {
                set({
                    selectedGarment: {
                        ...currentGarment, image: null
                    }
                });
            }
        } catch (err) {
          console.error("Error eliminando la imagen del garment:", err);
          set({ error: "Error eliminando la imagen del garment" });
        }
    },

    replaceImage: async (garmentId: number, imageId: number, file: File) => {
        try {
            await garmentService.replaceGarmentImage(imageId, file);
            await get().loadGarmentById(garmentId);
        } catch (err) {
          console.error("Error reemplazando la imagen del garment:", err);
          set({ error: "Error reemplazando la imagen del garment" });
        }
    }
}));