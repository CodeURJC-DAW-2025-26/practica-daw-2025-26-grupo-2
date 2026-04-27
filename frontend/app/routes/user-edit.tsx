import { useActionState, useEffect } from "react";
import { redirect, useNavigate } from "react-router";
import type { Route } from "./+types/user-edit";
import { Container, Row, Col } from "react-bootstrap";
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
        const url = new URL(request.url);
        throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
    }

    const profileId = Number(params.id);

    if (!user.roles?.includes("ADMIN") && user.id !== profileId) {
        throw redirect(`/error?message=${encodeURIComponent("Acceso no autorizado")}`);
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
            navigate(`/error?message=${encodeURIComponent(err.message || "Error al editar el usuario")}`);
            return { success: false, error: null };
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
            <Container>
                <Row className="justify-content-center">
                    <Col xs={12} sm={11} md={9} lg={8}>
                        <UserForm
                            user={user}
                            formAction={formAction}
                            isPending={isPending}
                            error={state.error}
                        />
                    </Col>
                </Row>
            </Container>
        </main>
    );
}