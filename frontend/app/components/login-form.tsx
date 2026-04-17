import { useState } from "react";
import { Form } from "react-bootstrap";
import { Link } from "react-router";

interface LoginFormProps {
    actionState: [
        { success: boolean; error: string | null } | null,
        (formData : FormData) => void,
        boolean,
    ];
    onCancel: () => void;
}

export default function LoginForm({
    actionState: [state, formAction, isPending],
    onCancel,
}: LoginFormProps) {

    const [showPassword, setShowPassword] = useState(false);

    return (<Form action={formAction} noValidate>
      {state?.error && (
        <div className="alert alert-danger py-2" role="alert">
          {state.error}
        </div>
      )}

      <div className="mb-3">
        <label htmlFor="email" className="form-label">Email</label>
        <div className="input-group">
          <span className="input-group-text login-addon">
            <i className="bi bi-envelope" />
          </span>
          <input
            type="email"
            name="email"
            className="form-control"
            id="email"
            placeholder="tuemail@ejemplo.com"
            required
            disabled={isPending}
          />
        </div>
      </div>

      <div className="mb-2">
        <label htmlFor="password" className="form-label">Contraseña</label>
        <div className="input-group">
          <span className="input-group-text login-addon">
            <i className="bi bi-lock" />
          </span>
          <input
            type={showPassword ? "text" : "password"}
            className="form-control"
            id="password"
            name="password"
            placeholder="••••••••"
            minLength={8}
            required
            disabled={isPending}
          />
          <button
            type="button"
            className="btn login-eye"
            onClick={() => setShowPassword(!showPassword)}
            tabIndex={-1}
            disabled={isPending}
          >
            <i className={`bi ${showPassword ? "bi-eye-slash" : "bi-eye"}`} />
          </button>
        </div>
      </div>

      <div className="d-grid mt-4">
        <button type="submit" className="btn btn-primary btn-lg" disabled={isPending}>
          {isPending ? "Entrando..." : "Entrar"}
        </button>
      </div>

      <div className="login-divider"><span>o</span></div>

      <div className="d-grid gap-2">
        <Link to="/" className="btn login-secondary">
          Volver a la tienda
        </Link>
      </div>

      <p className="login-footer">
        ¿No tienes cuenta?{" "}
        <Link to="/register" className="login-link">Crear cuenta</Link>
      </p>
    </Form>
  );
}
        