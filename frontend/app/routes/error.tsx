import { useSearchParams, useNavigate } from "react-router";
import { Container, Button } from "react-bootstrap";

export default function ErrorPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const message = searchParams.get("message") || "Ha ocurrido un error inesperado.";

  const is403 =
    message.toLowerCase().includes("autorizado") ||
    message.toLowerCase().includes("permiso");

  return (
    <Container className="container-main mt-5 mb-5">
      <div
        className="d-flex flex-column align-items-center justify-content-center text-center"
        style={{ minHeight: "50vh", gap: "1.25rem" }}
      >
        {/* Faded label */}
        <div
          style={{
            fontSize: "6rem",
            fontWeight: 900,
            lineHeight: 1,
            color: is403 ? "var(--bs-warning, #ffc107)" : "var(--bs-danger, #dc3545)",
            opacity: 0.12,
            userSelect: "none",
            marginBottom: "-2.5rem",
          }}
        >
          {is403 ? "403" : "Error"}
        </div>

        {/* Icon */}
        <i
          className={`bi ${is403 ? "bi-shield-lock" : "bi-exclamation-circle"}`}
          style={{
            fontSize: "3.5rem",
            color: is403
              ? "var(--bs-warning, #ffc107)"
              : "var(--bs-danger, #dc3545)",
          }}
        />

        <div>
          <h1 style={{ fontWeight: 800, fontSize: "1.6rem" }}>
            {is403 ? "Acceso no autorizado" : "Algo salió mal"}
          </h1>
          <p className="text-muted mt-2" style={{ maxWidth: 420, margin: "0.5rem auto 0" }}>
            {message}
          </p>
        </div>

        <div className="d-flex gap-3 flex-wrap justify-content-center mt-2">
          <Button variant="outline-secondary" onClick={() => navigate(-1)}>
            <i className="bi bi-arrow-left me-2" />
            Volver atrás
          </Button>
          <Button variant="primary" onClick={() => navigate("/")}>
            <i className="bi bi-house me-2" />
            Ir al inicio
          </Button>
        </div>
      </div>
    </Container>
  );
}