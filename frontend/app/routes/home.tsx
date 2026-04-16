import "bootstrap/dist/css/bootstrap.min.css";
import "./home.css";
import { Outlet, useNavigate, useNavigation } from "react-router";
import { useEffect } from "react";
import Header from "~/components/header";
import Footer from "~/components/footer";
import type { Route } from "./+types/home";
import Container from "react-bootstrap/Container";
import Alert from "react-bootstrap/Alert";
import Button from "react-bootstrap/Button";
import { useUserStore } from "~/stores/garment-store";

export default function Home() {
  const navigation = useNavigation();
  const isLoading = navigation.state === "loading";
  const { garments, loadGarments } = useUserStore();

  useEffect(() => { loadGarments({ 
            nameSearch: "", categorySearch: "", minPrice: 0, 
            maxPrice: 1000, sort: "id", page: 0, size: 10 
        }) }, []);

  return (
    <>
      {isLoading && (
        <div className="page-spinner-overlay">
          <div className="dot-spinner" />
        </div>
      )}

      <Header />
      <Outlet />
      <Footer />
    </>
  );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  const navigate = useNavigate();

  let errorMessage =
    error instanceof Error ? error.message : "Ocurrió un error inesperado.";

  return (
    <>
      <Header />
      <Container className="mt-4">
        <Alert variant="danger">
          <Alert.Heading>Error</Alert.Heading>
          <p>{errorMessage}</p>
          <Button variant="outline-danger" onClick={() => navigate("/")}>
            Volver al inicio
          </Button>
        </Alert>
      </Container>
      <Footer />

    </>
  );
}