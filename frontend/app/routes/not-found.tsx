import { useNavigate } from "react-router";
import { Container, Alert } from "react-bootstrap";

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <Container className="mt-4 mb-5">
      <Alert variant="danger">
        <Alert.Heading>Error</Alert.Heading>
        <p>La página que buscas no existe</p>
        <hr />
        <Alert.Link href="#" onClick={(e) => { e.preventDefault(); navigate("/"); }}>
          Volver al inicio
        </Alert.Link>
      </Alert>
    </Container>
  );
}