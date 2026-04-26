import { useActionState, useEffect } from "react";
import { useNavigate } from "react-router";
import UserForm from "~/components/user-form";
import { Container, Row, Col } from "react-bootstrap";
import { 
    addUser,
    uploadUserImage
    } from "~/services/users-service";
import { logIn } from "~/services/login-service";

export default function UserNew() {
    const navigate = useNavigate();

    async function userAction(prevState: any, formData: FormData) {
        const name = formData.get("name") as string;
        const surname = formData.get("surname") as string;
        const email = formData.get("email") as string;
        const address = formData.get("address") as string;
        const encodedPassword = formData.get("encodedPassword") as string;
        const imageFile = formData.get("imageFile") as File;

        try {
            const newUser = await addUser(name, surname, email, address, encodedPassword);
            await logIn(email, encodedPassword);

            if (imageFile && imageFile.size > 0) {
                await uploadUserImage(newUser.id, imageFile);
            }
            return { success: true, error: null };
        } catch (err: any) {
            navigate(`/error?message=${encodeURIComponent(err.message || "Error al registrar al usuario")}`);
            return { success: false, error: null };
        }
    }

    const [state, formAction, isPending] = useActionState(userAction, {
        success: false,
        error: null,
    });

    useEffect(() => {
        if (state.success) {
        navigate("/");
        }
    }, [state.success, navigate]);

    return (
        <main className="login-page">
            <Container>
                <Row className="justify-content-center">
                    <Col xs={12} sm={11} md={9} lg={8}>
                        <UserForm
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