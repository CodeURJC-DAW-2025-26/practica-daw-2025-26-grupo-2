import { useActionState, useEffect } from "react";
import { useNavigate } from "react-router";
import type { Route } from "./+types/user-edit";
import { Container, Row, Col } from "react-bootstrap";
import UserForm from "~/components/user-form";
import { requireAuth, requireOwnerOrRole } from "~/services/auth-service";
import { 
    getUser,
    updateUser,
    replaceUserImage,
    uploadUserImage
    } from "~/services/users-service";

export async function clientLoader({ request, params }: Route.ClientLoaderArgs) {
    const user = await requireAuth(request);

    const profileId = Number(params.id);

    requireOwnerOrRole(user, profileId, "ADMIN");

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