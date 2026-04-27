import { useState } from "react";
import { Form, Button, InputGroup, Alert } from "react-bootstrap";
import { Link } from "react-router";

interface LoginFormProps {
    actionState: [
        { success: boolean; error: string | null } | null,
        (formData: FormData) => void,
        boolean,
    ];
    onCancel: () => void;
}

export default function LoginForm({
    actionState: [state, formAction, isPending],
    onCancel,
}: LoginFormProps) {

    const [showPassword, setShowPassword] = useState(false);

    return (
        <Form action={formAction} noValidate>
            {state?.error && (
                <Alert variant="danger" className="py-2">
                    {state.error}
                </Alert>
            )}

            {/* EMAIL */}
            <Form.Group className="mb-3" controlId="email">
                <Form.Label>Email</Form.Label>
                <InputGroup>
                    <InputGroup.Text className="login-addon">
                        <i className="bi bi-envelope" />
                    </InputGroup.Text>
                    <Form.Control
                        type="email"
                        name="email"
                        placeholder="tuemail@ejemplo.com"
                        required
                        disabled={isPending}
                    />
                </InputGroup>
            </Form.Group>

            {/* PASSWORD */}
            <Form.Group className="mb-2" controlId="password">
                <Form.Label>Contraseña</Form.Label>
                <InputGroup>
                    <InputGroup.Text className="login-addon">
                        <i className="bi bi-lock" />
                    </InputGroup.Text>
                    <Form.Control
                        type={showPassword ? "text" : "password"}
                        name="password"
                        placeholder="••••••••"
                        minLength={8}
                        required
                        disabled={isPending}
                    />
                    <Button
                        variant="light"
                        className="login-eye"
                        onClick={() => setShowPassword(!showPassword)}
                        tabIndex={-1}
                        disabled={isPending}
                    >
                        <i className={`bi ${showPassword ? "bi-eye-slash" : "bi-eye"}`} />
                    </Button>
                </InputGroup>
            </Form.Group>

            {/* SUBMIT */}
            <div className="d-grid mt-4">
                <Button type="submit" variant="primary" size="lg" disabled={isPending}>
                    {isPending ? "Entrando..." : "Entrar"}
                </Button>
            </div>

            {/* DIVIDER */}
            <div className="login-divider">
                <span>o</span>
            </div>

            {/* BACK */}
            <div className="d-grid gap-2">
                <Button as={Link as any} to="/" className="login-secondary">
                    Volver a la tienda
                </Button>
            </div>

            {/* FOOTER */}
            <p className="login-footer">
                ¿No tienes cuenta?{" "}
                <Link to="/register" className="login-link">
                    Crear cuenta
                </Link>
            </p>
        </Form>
    );
}