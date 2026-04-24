import { useActionState, useEffect } from "react";
import { useNavigate } from "react-router";
import type { Route } from "./+types/garment-new";
import GarmentForm from "~/components/garment-form";
import { requireAuth, requireRole } from "~/services/auth-service";
import { 
    addGarment,
    uploadGarmentImage 
} from "~/services/garments-service";

export async function clientLoader({}: Route.ClientLoaderArgs) {
    requireAuth();
    requireRole("ADMIN");
    return null;
}

export default function GarmentNew() {
    const navigate = useNavigate();

    async function garmentAction(prevState: any, formData: FormData) {
        const name = formData.get("name") as string;
        const price = formData.get("price") as string;
        const category = formData.get("category") as string;
        const description = formData.get("description") as string;
        const features = formData.get("features") as string;
        const imageFile = formData.get("imageFile") as File;

        try {
            const newGarment = await addGarment(name, Number(price), category, description, features);

            if (imageFile && imageFile.size > 0) {
                await uploadGarmentImage(newGarment.id, imageFile);
            }
            return { success: true, error: null, garmentId: newGarment.id };
        } catch (err: any) {
            return { success: false, error: err.message || "Error al guardar la prenda", garmentId: null };
        }
    }

    const [state, formAction, isPending] = useActionState(garmentAction, {
        success: false,
        error: null,
        garmentId: null as number | null,
    });

    useEffect(() => {
        if (state.success && state.garmentId != null) {
            navigate(`/garment/${state.garmentId}`);
        }
    }, [state.success, state.garmentId, navigate]);

    return (
        <main className="mt-5">
            <GarmentForm
                formAction={formAction} 
                isPending={isPending} 
                error={state.error} 
            />
        </main>
    );
}