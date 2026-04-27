import { useActionState, useEffect } from "react";
import { useNavigate, redirect } from "react-router";
import type { Route } from "./+types/garment-new";
import GarmentForm from "~/components/garment-form";
import { useUserStore } from "~/stores/user-store";
import { Container } from "react-bootstrap";
import { 
    addGarment,
    uploadGarmentImage 
} from "~/services/garments-service";

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
    const url = new URL(request.url);
    await useUserStore.getState().loadLoggedUser();
    const { user } = useUserStore.getState();
    if (!user) {
        throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
    }
    if (!user.roles?.includes("ADMIN")) {
        throw redirect(`/error?message=${encodeURIComponent("Acceso no autorizado")}`);
    }
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
            navigate(`/error?message=${encodeURIComponent(err.message || "Error al guardar la prenda")}`);
            return { success: false, error: null, garmentId: null };
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
        <main>
            <Container className="container-main mt-5">
                <GarmentForm
                    formAction={formAction} 
                    isPending={isPending} 
                    error={state.error} 
                />
            </Container>
        </main>
    );
}