import "~/routes/login.css";
import { useState } from "react";
import { Link } from "react-router";
import { Form, Button, InputGroup } from "react-bootstrap";
import type UserExtendedDTO from "~/dtos/UserExtendedDTO";

interface UserFormProps {
    user?: UserExtendedDTO;
    formAction: (payload: FormData) => void;
    isPending: boolean;
    error: string | null;
}

export default function UserForm({ user, formAction, isPending, error }: UserFormProps) {
    const isNew = !user;
    const [validated, setValidated] = useState(false);
    const [updateImage, setUpdateImage] = useState(false);
    const [updatePassword, setUpdatePassword] = useState(false);

    const handleSubmit = (event: React.SubmitEvent<HTMLFormElement>) => {
        const form = event.currentTarget;

        // If form si not valid
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }

        setValidated(true);
    };

    return (
        <section className="login-card">
            <div className="login-header">
                {error && <div className="alert alert-danger">{error}</div>}
                <h1 className="login-title">
                    {isNew ? "Crear cuenta" : "Editar perfil"}
                </h1>
            </div>

            <Form
                action={formAction}
                method="post"
                noValidate
                validated={validated}
                onSubmit={handleSubmit}
            >
                {/* Only ID for edition */}
                {!isNew && <input type="hidden" name="id" value={user.id} />}

                {/* AVATAR */}
                {!isNew && (
                    <Form.Check
                        type="checkbox"
                        id="updateImage"
                        name="updateImage"
                        label="Actualizar imagen"
                        className="mb-2 d-inline-block"
                        checked={updateImage}
                        onChange={(e) => setUpdateImage(e.target.checked)}
                    />
                )}

                <div className="text-center mb-3">
                    {!isNew && user.avatar?.id ? (
                        <img
                            src={`/api/v1/images/${user.avatar.id}/media`}
                            alt="Tu Avatar"
                            className="rounded-circle mb-2"
                            width="96"
                            height="96"
                            style={{ objectFit: "cover" }}
                        />
                    ) : (
                        <img
                            src="/avatar_default.png"
                            alt="Avatar por defecto"
                            className="rounded-circle mb-2"
                            width="96"
                            height="96"
                        />
                    )}

                    <Form.Group className="mb-2">
                        <Form.Label>Imagen de perfil</Form.Label>
                        <Form.Control
                            type="file"
                            name="imageFile"
                            accept=".jpg,.jpeg,.png"
                            disabled={!isNew && !updateImage}
                        />
                    </Form.Group>
                </div>

                {/* NAME */}
                <Form.Group className="mb-3">
                    <Form.Label>Nombre</Form.Label>
                    <InputGroup>
                        <InputGroup.Text className="login-addon">
                            <i className="bi bi-person"></i>
                        </InputGroup.Text>
                        <Form.Control
                            type="text"
                            name="name"
                            placeholder="Tu nombre"
                            defaultValue={user?.name || ""}
                            minLength={4}
                            maxLength={50}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            El nombre debe tener entre 4 y 50 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* SURNAME */}
                <Form.Group className="mb-3">
                    <Form.Label>Apellidos</Form.Label>
                    <InputGroup>
                        <InputGroup.Text className="login-addon">
                            <i className="bi bi-person-lines-fill"></i>
                        </InputGroup.Text>
                        <Form.Control
                            type="text"
                            name="surname"
                            placeholder="Tus apellidos"
                            defaultValue={user?.surname || ""}
                            minLength={5}
                            maxLength={100}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            Los apellidos deben tener entre 5 y 100 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* EMAIL */}
                <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <InputGroup>
                        <InputGroup.Text className="login-addon">
                            <i className="bi bi-envelope"></i>
                        </InputGroup.Text>
                        <Form.Control
                            type="email"
                            name="email"
                            placeholder="tuemail@ejemplo.com"
                            defaultValue={user?.email || ""}
                            minLength={3}
                            maxLength={90}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            Introduce un email válido entre 3 y 90 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* PASSWORD */}
                {!isNew && (
                    <Form.Check
                        type="checkbox"
                        id="updatePassword"
                        name="updatePassword"
                        label="Actualizar contraseña"
                        className="mb-2"
                        checked={updatePassword}
                        onChange={(e) => setUpdatePassword(e.target.checked)}
                    />
                )}

                <Form.Group className="mb-3">
                    <Form.Label>Contraseña</Form.Label>
                    <InputGroup>
                        <InputGroup.Text className="login-addon">
                            <i className="bi bi-lock"></i>
                        </InputGroup.Text>
                        <Form.Control
                            type="password"
                            name="encodedPassword"
                            placeholder="••••••••"
                            minLength={8}
                            maxLength={100}
                            required={isNew || updatePassword}
                            disabled={!isNew && !updatePassword}
                        />
                        <Form.Control.Feedback type="invalid">
                            La contraseña debe tener entre 8 y 100 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                {/* ADDRESS */}
                <Form.Group className="mb-3">
                    <Form.Label>Dirección</Form.Label>
                    <InputGroup>
                        <InputGroup.Text className="login-addon">
                            <i className="bi bi-geo-alt"></i>
                        </InputGroup.Text>
                        <Form.Control
                            type="text"
                            name="address"
                            placeholder="C/ Ejemplo 123, 28000"
                            defaultValue={user?.address || ""}
                            minLength={10}
                            maxLength={150}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            La dirección debe tener entre 10 y 150 caracteres.
                        </Form.Control.Feedback>
                    </InputGroup>
                </Form.Group>

                <div className="d-grid mt-4">
                    <Button type="submit" size="lg" disabled={isPending}>
                        {isPending
                            ? "Procesando..."
                            : isNew
                                ? "Crear cuenta"
                                : "Guardar cambios"}
                    </Button>
                </div>
            </Form>

            {isNew && (
                <>
                    <div className="login-divider"><span>o</span></div>
                    <div className="d-grid gap-2">
                        <Button
                            as={Link as any}
                            to="/"
                            variant="secondary"
                            className="login-secondary"
                        >
                            Volver a la tienda
                        </Button>
                    </div>
                    <p className="login-footer mt-3">
                        ¿Ya tienes cuenta?{" "}
                        <Link to="/login" className="login-link">
                            Iniciar sesión
                        </Link>
                    </p>
                </>
            )}
        </section>
    );
}