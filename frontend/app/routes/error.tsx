import { useSearchParams, useNavigate } from "react-router";
import { Container, Alert, Button } from "react-bootstrap";

export default function ErrorPage() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const message = searchParams.get("message") || "Ha ocurrido un error";

    return (
        <Container className="mt-4 mb-5">
            <Alert variant="danger">
                <strong>Error:</strong> {message}
            </Alert>
            <div className="mt-4">
                <Button variant="primary" onClick={() => navigate("/")}>Volver</Button>
            </div>
        </Container>
    );
}
