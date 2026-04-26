import { useLoaderData, redirect, useSearchParams } from "react-router";
import type { Route } from "./+types/statistics";
import { Container, Row, Col } from "react-bootstrap";
import { useUserStore } from "~/stores/user-store";
import Statistics from "~/components/statistics";
import { getIncomeStatistics, getOrdersStatistics, getLabelsStatistics } from "~/services/statistics-service";

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  // Ensure user is loaded before checking roles
  let { user } = useUserStore.getState();
  if (!user) {
    await useUserStore.getState().loadLoggedUser();
    user = useUserStore.getState().user;
  }

  if (!user) {
    const url = new URL(request.url);
    throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
  }

  if (!user.roles?.includes("ADMIN")) {
    throw redirect("/");
  }

  const url = new URL(request.url);
  const period = url.searchParams.get("period") || "month";
  const count = period === "year" ? 5 : 12;

  // Parallel data fetching
  const [income, orders, labels] = await Promise.all([
    getIncomeStatistics(period, count),
    getOrdersStatistics(period, count),
    getLabelsStatistics(period, count)
  ]);

  return { 
    userId: user.id,
    period,
    income,
    orders,
    labels
  };
}

export default function StatisticsPage({ loaderData }: Route.ComponentProps) {
  const { userId, income, orders, labels, period } = loaderData;

  return (
    <Container className="container-main mt-5 mb-5">
      <div className="text-center mb-5">
        <h2 className="title">Panel de Estadísticas</h2>
        <p className="text-muted">Visualización de ingresos, pedidos y actividad de usuarios</p>
      </div>

      <Row>
        <Col xs={12}>
          <Statistics 
            userId={userId} 
            initialIncome={income}
            initialOrders={orders}
            initialLabels={labels}
            currentPeriod={period}
          />
        </Col>
      </Row>
    </Container>
  );
}
