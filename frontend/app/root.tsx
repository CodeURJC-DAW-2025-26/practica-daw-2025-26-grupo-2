import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  useNavigation,
} from "react-router";
import { Container, Alert, Button } from "react-bootstrap";
import { Link } from "react-router";

import { ApiError } from "~/services/api-fetch";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";

import type { Route } from "./+types/root";
import "./app.css";

export function HydrateFallback() {
  return (
    <div className="page-spinner-overlay">
      <div className="dot-spinner" />
    </div>
  );
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
  let details = "Ha ocurrido un error inesperado";
  let stack: string | undefined;
  let is403 = false;
  let is404 = false;

  if (isRouteErrorResponse(error)) {
    details = error.statusText || details;
  } else if (error instanceof ApiError) {
    details = error.message || details;
  } else if (error instanceof Error) {
    details = error.message || details;
  }

  return (
    <main style={{ padding: "1.5rem" }}>
      <Container className="mt-4 mb-5">
        <Alert variant="danger">
          <Alert.Heading>Error</Alert.Heading>
          <p>{details}</p>
          <hr />

          <Button as={Link as any} to="/" variant="link" className="p-0">
            Volver al inicio
          </Button>
        </Alert>
      </Container>
    </main>
  );
}