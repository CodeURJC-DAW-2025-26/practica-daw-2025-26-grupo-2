import { useSearchParams, useNavigate } from "react-router";
import { Container, Alert } from "react-bootstrap";

export default function ErrorPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const message = searchParams.get("message") || "Ha ocurrido un error inesperado";

  return (
    <Container className="mt-4 mb-5">
      <Alert variant="danger">
        <Alert.Heading>Error</Alert.Heading>
        <p>{message}</p>
        <hr />
        <Alert.Link href="#" onClick={(e) => { e.preventDefault(); navigate("/"); }}>
          Volver al inicio
        </Alert.Link>
      </Alert>
    </Container>
  );
}