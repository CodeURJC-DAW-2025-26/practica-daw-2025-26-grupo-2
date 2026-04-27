import type { Route } from "./+types/statistics";
import { Container, Row, Col } from "react-bootstrap";
import { requireAuth, requireRole } from "~/services/auth-service";
import Statistics from "~/components/statistics";
import { getIncomeStatistics, getOrdersStatistics, getCategoryStatistics, getLabelsStatistics } from "~/services/statistics-service";

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const user = await requireAuth(request);
  requireRole(user, "ADMIN");

  const url = new URL(request.url);
  const period = url.searchParams.get("period") || "month";
  const count = period === "year" ? 5 : 12;

  const [income, orders, categories, labels] = await Promise.all([
    getIncomeStatistics(period, count),
    getOrdersStatistics(period, count),
    getCategoryStatistics(),
    getLabelsStatistics(period, count)
  ]);

  return { 
    userId: user.id,
    period,
    income,
    orders,
    categories,
    labels
  };
}

export default function StatisticsPage({ loaderData }: Route.ComponentProps) {
  const { userId, income, orders, categories, period, labels } = loaderData;

  return (
    <Container className="container-main mt-5 mb-5">
      <div className="text-center mb-5">
        <h2 className="title">Panel de Estadísticas</h2>
      </div>

      <Row>
        <Col xs={12}>
          <Statistics 
            userId={userId} 
            initialIncome={income}
            initialOrders={orders}
            categoryData={categories}
            timelineLabels={labels}
            currentPeriod={period}
          />
        </Col>
      </Row>
    </Container>
  );
}
