import "~/routes/login.css";
import { useState } from "react";
import { Link } from "react-router";
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
        if (form.checkValidity() === false) {
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

            <form 
                action={formAction} 
                method="post" 
                noValidate 
                onSubmit={handleSubmit}
                className={`needs-validation ${validated ? "was-validated" : ""}`}
            >
                {/* Only ID for edition */}
                {!isNew && <input type="hidden" name="id" value={user.id} />}

                {/* Avatar section */}
                {!isNew && (
                    <div className="form-check d-inline-block mb-2">
                        <input 
                            className="form-check-input" 
                            type="checkbox" 
                            id="updateImage" 
                            name="updateImage"
                            checked={updateImage}
                            onChange={(e) => setUpdateImage(e.target.checked)}
                        />
                        <label className="form-check-label" htmlFor="updateImage">Actualizar imagen</label>
                    </div>
                )}

                <div className="text-center mb-3">
                    {!isNew && user.avatar?.id ? (
                    <img 
                        src={`/api/v1/images/${user.avatar.id}/media`} 
                        alt="Tu Avatar" 
                        className="rounded-circle mb-2" 
                        width="96" height="96" style={{ objectFit: "cover" }} 
                    />
                    ) : (
                    <img 
                        src="/avatar_default.png" 
                        alt="Avatar por defecto" 
                        className="rounded-circle mb-2" 
                        width="96" height="96" 
                    />
                    )}

                    <div className="mb-2">
                        <label htmlFor="avatar" className="form-label d-block">Imagen de perfil</label>
                        <input 
                        type="file" 
                        id="avatar" 
                        name="imageFile" 
                        className="form-control" 
                        accept=".jpg,.jpeg,.png"
                        disabled={!isNew && !updateImage}
                        />
                    </div>
                </div>

                {/* Name */}
                <div className="mb-3">
                    <label htmlFor="name" className="form-label">Nombre</label>
                    <div className="input-group">
                        <span className="input-group-text login-addon"><i className="bi bi-person"></i></span>
                        <input 
                        type="text" id="name" name="name" className="form-control" 
                        placeholder="Tu nombre" defaultValue={user?.name || ""}
                        minLength={4} maxLength={50} required 
                        />
                        <div className="invalid-feedback">El nombre debe tener entre 4 y 50 caracteres.</div>
                    </div>
                </div>

                {/* Surname */}
                <div className="mb-3">
                    <label htmlFor="surname" className="form-label">Apellidos</label>
                    <div className="input-group">
                        <span className="input-group-text login-addon"><i className="bi bi-person-lines-fill"></i></span>
                        <input 
                        type="text" id="surname" name="surname" className="form-control" 
                        placeholder="Tus apellidos" defaultValue={user?.surname || ""}
                        minLength={5} maxLength={100} required 
                        />
                        <div className="invalid-feedback">Los apellidos deben tener entre 5 y 100 caracteres.</div>
                    </div>
                </div>

                {/* Email */}
                <div className="mb-3">
                    <label htmlFor="email" className="form-label">Email</label>
                    <div className="input-group">
                        <span className="input-group-text login-addon"><i className="bi bi-envelope"></i></span>
                        <input 
                        type="email" id="email" name="email" className="form-control" 
                        placeholder="tuemail@ejemplo.com" defaultValue={user?.email || ""}
                        minLength={3} maxLength={90} required 
                        />
                        <div className="invalid-feedback">Introduce un email válido entre 3 y 90 caracteres.</div>
                    </div>
                </div>

                {/* Password */}
                {!isNew && (
                    <div className="form-check mb-2">
                        <input 
                        className="form-check-input" type="checkbox" id="updatePassword" 
                        name="updatePassword" checked={updatePassword}
                        onChange={(e) => setUpdatePassword(e.target.checked)}
                        />
                        <label className="form-check-label" htmlFor="updatePassword">Actualizar contraseña</label>
                    </div>
                )}
                <div className="mb-3">
                    <label htmlFor="password" className="form-label">Contraseña</label>
                    <div className="input-group">
                        <span className="input-group-text login-addon"><i className="bi bi-lock"></i></span>
                        <input 
                        type="password" id="password" name="encodedPassword" 
                        className="form-control" placeholder="••••••••" 
                        minLength={8} maxLength={100}
                        required={isNew || updatePassword}
                        disabled={!isNew && !updatePassword}
                        />
                        <div className="invalid-feedback">La contraseña debe tener entre 8 y 100 caracteres.</div>
                    </div>
                </div>

                {/* Address */}
                <div className="mb-3">
                    <label htmlFor="address" className="form-label">Dirección</label>
                    <div className="input-group">
                        <span className="input-group-text login-addon"><i className="bi bi-geo-alt"></i></span>
                        <input 
                        type="text" id="address" name="address" className="form-control" 
                        placeholder="C/ Ejemplo 123, 28000" defaultValue={user?.address || ""}
                        minLength={10} maxLength={150} required
                        />
                        <div className="invalid-feedback">La dirección debe tener entre 10 y 150 caracteres.</div>
                    </div>
                </div>

                <div className="d-grid mt-4">
                    <button type="submit" className="btn btn-primary btn-lg" disabled={isPending}>
                        {isPending ? "Procesando..." : (isNew ? "Crear cuenta" : "Guardar cambios")}
                    </button>
                </div>
            </form>

            {isNew && (
                <>
                    <div className="login-divider"><span>o</span></div>
                    <div className="d-grid gap-2">
                        <Link to="/" className="btn btn-secondary login-secondary">Volver a la tienda</Link>
                    </div>
                    <p className="login-footer mt-3">
                        ¿Ya tienes cuenta? <Link to="/login" className="login-link">Iniciar sesión</Link>
                    </p>
                </>
            )}
        </section>
    );
}