import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  useNavigation,
} from "react-router";
import { ApiError } from "~/services/api-fetch";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";

import type { Route } from "./+types/root";
import "./app.css";

export function HydrateFallback() {
  return <p>Loading...</p>;
}

export function Layout({ children }: { children: React.ReactNode }) {
  const navigation = useNavigation();
  const isLoading = navigation.state === "loading";

  return (
    <html lang="en">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body>
        {isLoading && (
          <div className="page-spinner-overlay">
            <div className="dot-spinner" />
          </div>
        )}
        {children}
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  return <Outlet />;
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  let code: number = 500;
  let title = "Algo salió mal";
  let details = "Ha ocurrido un error inesperado.";
  let stack: string | undefined;
  let is403 = false;
  let is404 = false;

  if (isRouteErrorResponse(error)) {
    code = error.status;
    is403 = code === 403;
    is404 = code === 404;
    if (is404) {
      title = "Página no encontrada";
      details = "Te has perdido en el ciberespacio. Esta página no existe.";
    } else if (is403) {
      title = "Acceso no autorizado";
      details = "No tienes permisos para acceder a este recurso.";
    } else {
      title = `Error ${code}`;
      details = error.statusText || details;
    }
  } else if (error instanceof ApiError) {
    // Errors thrown by apiFetch (e.g. 404 from the API — "no encontrado en BD")
    code = error.status;
    is403 = code === 403;
    is404 = code === 404;
    if (is404) {
      title = "No encontrado";
      details = error.message || "El recurso que buscas no existe en la base de datos.";
    } else if (is403) {
      title = "Acceso no autorizado";
      details = "No tienes permisos para acceder a este recurso.";
    } else {
      title = `Error ${code}`;
      details = error.message || details;
    }
  } else if (error instanceof Error) {
    details = error.message;
    if (import.meta.env.DEV) {
      stack = error.stack;
    }
  }

  const iconClass = is403 ? "bi-shield-lock" : is404 ? "bi-compass" : "bi-exclamation-circle";
  const accentColor = is403 ? "#ffc107" : "#dc3545";

  return (
    <main
      style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "2rem",
        fontFamily: "inherit",
      }}
    >
      <div style={{ maxWidth: 480, width: "100%", textAlign: "center" }}>
        <div
          style={{
            fontSize: "8rem",
            fontWeight: 900,
            lineHeight: 1,
            color: accentColor,
            opacity: 0.12,
            userSelect: "none",
            marginBottom: "-3rem",
          }}
        >
          {code}
        </div>

        <i
          className={`bi ${iconClass}`}
          style={{
            fontSize: "3.5rem",
            color: accentColor,
            display: "block",
            marginBottom: "1.25rem",
          }}
        />

        <h1 style={{ fontWeight: 800, fontSize: "1.6rem", marginBottom: "0.5rem" }}>
          {title}
        </h1>
        <p style={{ color: "#6c757d", marginBottom: "2rem" }}>{details}</p>

        {stack && (
          <pre
            style={{
              textAlign: "left",
              background: "#f8f9fa",
              border: "1px solid #dee2e6",
              borderRadius: 8,
              padding: "1rem",
              fontSize: "0.78rem",
              overflowX: "auto",
              marginBottom: "2rem",
            }}
          >
            <code>{stack}</code>
          </pre>
        )}

        <div style={{ display: "flex", gap: "0.75rem", justifyContent: "center", flexWrap: "wrap" }}>
          <a href="javascript:history.back()" className="btn btn-outline-secondary">
            <i className="bi bi-arrow-left me-2" />
            Volver atrás
          </a>
          <a href="/" className="btn btn-primary">
            <i className="bi bi-house me-2" />
            Ir al inicio
          </a>
        </div>
      </div>
    </main>
  );
}