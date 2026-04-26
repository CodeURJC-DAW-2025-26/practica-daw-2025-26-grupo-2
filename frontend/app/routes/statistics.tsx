import { useLoaderData, redirect } from "react-router";
import type { Route } from "./+types/statistics";
import { Container, Row, Col, Alert } from "react-bootstrap";
import { useUserStore } from "~/stores/user-store";
import Statistics from "~/components/statistics";

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const { user } = useUserStore.getState();

  if (!user) {
    const url = new URL(request.url);
    throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
  }

  if (!user.roles?.includes("ADMIN")) {
    throw redirect("/");
  }

  return { userId: user.id };
}

export default function StatisticsPage() {
  const { userId } = useLoaderData<typeof clientLoader>();

  return (
    <Container className="container-main mt-5 mb-5">
      <div className="text-center mb-5">
        <h2 className="title">Panel de Estadísticas</h2>
        <p className="text-muted">Visualización de ingresos, pedidos y actividad de usuarios</p>
      </div>

      <Row>
        <Col xs={12}>
          <Statistics userId={userId} />
        </Col>
      </Row>
    </Container>
  );
}
