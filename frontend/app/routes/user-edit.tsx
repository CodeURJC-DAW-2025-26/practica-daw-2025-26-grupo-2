import { useActionState, useEffect } from "react";
import { redirect, useNavigate } from "react-router";
import type { Route } from "./+types/user-edit";
import UserForm from "~/components/user-form";
import { useUserStore } from "~/stores/user-store";
import { 
    getUser,
    updateUser,
    replaceUserImage,
    uploadUserImage
    } from "~/services/users-service";

export async function clientLoader({ request, params }: Route.ClientLoaderArgs) {
    await useUserStore.getState().loadLoggedUser();
    const { user } = useUserStore.getState();

    if (!user) {
        //const url = new URL(request.url);
        throw redirect("/login"); //mirar que redirija o no a la página de edición de usuario después
    }

    const profileId = Number(params.id);

    if (!user.roles?.includes("ADMIN") && user.id !== profileId) {
        throw redirect("/unauthorized");
    }

    const profileUser = await getUser(profileId);
    return profileUser;
}

export default function UserEdit({ loaderData }: Route.ComponentProps) {
    const navigate = useNavigate();
    const user = loaderData;

    async function userAction(prevState: any, formData: FormData) {
        const id = formData.get("id") as string;
        const name = formData.get("name") as string;
        const surname = formData.get("surname") as string;
        const email = formData.get("email") as string;
        const address = formData.get("address") as string;
        const encodedPassword = formData.get("encodedPassword") as string;
        const imageFile = formData.get("imageFile") as File;

        try {
            const user = await updateUser(Number(id), name, surname, email, address, encodedPassword);

            if (formData.get("updateImage") === "on" && imageFile && imageFile.size > 0) {
                if (user.avatar){
                    await replaceUserImage(user.avatar.id, imageFile);
                } else {
                    await uploadUserImage(Number(id), imageFile);
                }
            }
            return { success: true, error: null };
        } catch (err: any) {
            return { success: false, error: err.message || "Error al editar el usuario" };
        }
    }

    const [state, formAction, isPending] = useActionState(userAction, {
        success: false,
        error: null,
    });

    useEffect(() => {
        if (state.success) {
        navigate(`/users/${user.id}`);
        }
    }, [state.success, navigate]); //, user

    return (
        <main className="login-page">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-12 col-sm-11 col-md-9 col-lg-8">
                        <UserForm 
                        user={user} 
                        formAction={formAction} 
                        isPending={isPending} 
                        error={state.error} 
                        />
                    </div>
                </div>
            </div>
        </main>
    );
}