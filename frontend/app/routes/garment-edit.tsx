import { useActionState, useEffect} from "react";
import { useNavigate } from "react-router";
import type { Route } from "./+types/garment-edit";
import GarmentForm from "~/components/garment-form";
import { requireAuth, requireRole } from "~/services/auth-service";
import { 
    getGarment,
    updateGarment,
    replaceGarmentImage, 
    uploadGarmentImage 
} from "~/services/garments-service";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    requireAuth();
    requireRole("ADMIN");
    return await getGarment(Number(params.id));
}

export default function GarmentEdit({ loaderData }: Route.ComponentProps) {
    const navigate = useNavigate();
    const garment = loaderData;

    async function garmentAction(prevState: any, formData: FormData) {
        const id = formData.get("id") as string;
        const name = formData.get("name") as string;
        const price = formData.get("price") as string;
        const category = formData.get("category") as string;
        const description = formData.get("description") as string;
        const features = formData.get("features") as string;
        const imageFile = formData.get("imageFile") as File;

        try {
            const garment = await updateGarment(Number(id), name, Number(price), category, description, features);

            if (formData.get("updateImage") === "on" && imageFile && imageFile.size > 0) {
                if (garment.image){
                    await replaceGarmentImage(garment.image.id, imageFile);
                } else {
                    await uploadGarmentImage(Number(id), imageFile);
                }
            }
            return { success: true, error: null };
        } catch (err: any) {
            return { success: false, error: err.message || "Error al guardar la prenda" };
        }
    }

    const [state, formAction, isPending] = useActionState(garmentAction, {
        success: false,
        error: null,
    });

    useEffect(() => {
        if (state.success) {
            navigate(`/garment/${garment.id}`);
        }
    }, [state.success, navigate]);

    return (
        <main className="mt-5">
            <GarmentForm 
                garment={garment} 
                formAction={formAction} 
                isPending={isPending} 
                error={state.error} 
            />
        </main>
    );
}