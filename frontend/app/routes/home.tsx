import "bootstrap/dist/css/bootstrap.min.css";
import "./home.css";
import { Outlet, useNavigate, useNavigation } from "react-router";
import Header from "~/components/header";
import Footer from "~/components/footer";
import type { Route } from "./+types/home";
import Container from "react-bootstrap/Container";
import Alert from "react-bootstrap/Alert";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader() {
  await useUserStore.getState().loadLoggedUser();
  return null;
}

export default function Home() {
  return (
    <>
      <Header />
      <Outlet />
      <Footer />
    </>
  );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  const navigate = useNavigate();

  const errorMessage =
    error instanceof Error ? error.message : "Ocurrió un error inesperado";

  return (
    <>
      <Header />
      <Container className="mt-4 mb-5">
        <Alert variant="danger">
          <Alert.Heading>Error</Alert.Heading>
          <p>{errorMessage}</p>
          <hr />
          <Alert.Link href="#" onClick={(e) => { e.preventDefault(); navigate("/"); }}>
            Volver al inicio
          </Alert.Link>
        </Alert>
      </Container>
      <Footer />
    </>
  );
}