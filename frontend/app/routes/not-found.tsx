import { useNavigate } from "react-router";
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <Container className="text-center mt-5">
      <h1 className="display-1 fw-bold">404</h1>
      <p className="lead">La página que buscas no existe.</p>
      <Button onClick={() => navigate("/")} variant="dark">
        Volver al inicio
      </Button>
    </Container>
  );
}