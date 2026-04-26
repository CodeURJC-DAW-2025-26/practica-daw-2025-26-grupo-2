import { useSearchParams } from "react-router";
import { 
  Chart as ChartJS, 
  CategoryScale, 
  LinearScale, 
  PointElement, 
  LineElement, 
  Title, 
  Tooltip, 
  Legend, 
  BarElement,
  RadialLinearScale,
  ArcElement,
  Filler
} from 'chart.js';
import { Line, Bar, Doughnut } from 'react-chartjs-2';
import { Card, Row, Col, Form } from "react-bootstrap";
import "./statistics.css";

// Register Chart.js components globally
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  RadialLinearScale,
  ArcElement,
  Filler,
  Title,
  Tooltip,
  Legend
);

interface StatisticsProps {
  userId: number;
  initialIncome: number[];
  initialOrders: number[];
  categoryData: Record<string, number>;
  timelineLabels: string[];
  currentPeriod: string;
}

export default function Statistics({ initialIncome, initialOrders, categoryData, timelineLabels, currentPeriod }: StatisticsProps) {
  const [searchParams, setSearchParams] = useSearchParams();
  
  // KPI Calculations
  const totalIncome = initialIncome.reduce((acc, val) => acc + val, 0);
  const totalOrders = initialOrders.reduce((acc, val) => acc + val, 0);
  const avgTicket = totalOrders > 0 ? (totalIncome / totalOrders).toFixed(2) : "0.00";

  // Category chart logic
  const categoryNames = Object.keys(categoryData);
  const categoryValues = Object.values(categoryData);
  const hasCategoryData = categoryValues.length > 0 && categoryValues.some(val => val > 0);

  const incomeChartData = {
    labels: timelineLabels, // Real dates from backend (e.g., "26/04", "abr 2026")
    datasets: [
      {
        label: 'Ingresos (€)',
        data: initialIncome,
        borderColor: 'rgb(107, 79, 63)',
        backgroundColor: 'rgba(107, 79, 63, 0.2)',
        tension: 0.4,
        fill: true,
        pointRadius: 4,
        pointBackgroundColor: 'rgb(107, 79, 63)',
      },
    ],
  };

  const ordersChartData = {
    labels: timelineLabels, // Real dates from backend
    datasets: [
      {
        label: 'Número de Pedidos',
        data: initialOrders,
        backgroundColor: 'rgba(54, 162, 235, 0.7)',
        borderColor: 'rgb(54, 162, 235)',
        borderWidth: 1,
        borderRadius: 5,
      },
    ],
  };

  const doughnutChartData = {
    labels: categoryNames,
    datasets: [
      {
        label: 'Ventas por Categoría',
        data: categoryValues,
        backgroundColor: [
          'rgba(107, 79, 63, 0.8)',
          'rgba(54, 162, 235, 0.8)',
          'rgba(255, 206, 86, 0.8)',
          'rgba(75, 192, 192, 0.8)',
          'rgba(153, 102, 255, 0.8)',
          'rgba(201, 203, 207, 0.8)',
        ],
        hoverOffset: 15,
        borderWidth: 2,
        borderColor: '#ffffff',
      },
    ],
  };

  const handlePeriodChange = (e: React.ChangeEvent<any>) => {
    setSearchParams({ period: e.target.value });
  };

  return (
    <div className="statistics-container">
      {/* KPI CARDS */}
      <Row className="mb-4">
        <Col md={4} className="mb-3">
          <Card className="shadow-sm border-0 bg-white p-3 text-center">
            <div className="text-muted small text-uppercase fw-bold mb-1">Total Ingresos</div>
            <h3 className="fw-bold mb-0 text-dark">{totalIncome.toLocaleString('es-ES')}€</h3>
            <div className="text-success small mt-1"><i className="bi bi-graph-up"></i> Periodo actual</div>
          </Card>
        </Col>
        <Col md={4} className="mb-3">
          <Card className="shadow-sm border-0 bg-white p-3 text-center">
            <div className="text-muted small text-uppercase fw-bold mb-1">Total Pedidos</div>
            <h3 className="fw-bold mb-0 text-dark">{totalOrders}</h3>
            <div className="text-primary small mt-1"><i className="bi bi-cart-check"></i> Actividad real</div>
          </Card>
        </Col>
        <Col md={4} className="mb-3">
          <Card className="shadow-sm border-0 bg-white p-3 text-center">
            <div className="text-muted small text-uppercase fw-bold mb-1">Ticket Medio</div>
            <h3 className="fw-bold mb-0 text-dark">{avgTicket}€</h3>
            <div className="text-warning small mt-1"><i className="bi bi-calculator"></i> Por pedido</div>
          </Card>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col md={12} className="d-flex justify-content-between align-items-center mb-3">
          <h4 className="fw-bold mb-0 text-secondary">Gráficas de Tendencia</h4>
          <div className="d-flex align-items-center gap-2">
            <span className="fw-bold small text-muted">FILTRAR POR:</span>
            <Form.Select 
              size="sm" 
              style={{ width: '150px', borderRadius: '8px' }} 
              value={currentPeriod}
              onChange={handlePeriodChange}
            >
              <option value="day">Días</option>
              <option value="month">Meses</option>
              <option value="year">Años</option>
            </Form.Select>
          </div>
        </Col>

        {/* INCOME CHART */}
        <Col lg={8} className="mb-4">
          <Card className="shadow-sm border-0 h-100">
            <Card.Header className="bg-white py-3 border-0">
              <h5 className="mb-0 fw-bold"><i className="bi bi-cash-stack me-2"></i>Evolución de Ingresos</h5>
            </Card.Header>
            <Card.Body>
              <div style={{ height: '350px' }}>
                <Line 
                  data={incomeChartData} 
                  options={{ 
                    responsive: true, 
                    maintainAspectRatio: false,
                    plugins: {
                      legend: { display: false },
                    },
                    scales: {
                      y: { beginAtZero: true, grid: { color: '#f0f0f0' } },
                      x: { grid: { display: false } }
                    }
                  }} 
                />
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* DOUGHNUT CHART */}
        <Col lg={4} className="mb-4">
          <Card className="shadow-sm border-0 h-100">
            <Card.Header className="bg-white py-3 border-0">
              <h5 className="mb-0 fw-bold"><i className="bi bi-pie-chart me-2"></i>Por Categoría</h5>
            </Card.Header>
            <Card.Body className="d-flex flex-column align-items-center justify-content-center">
              {hasCategoryData ? (
                <div style={{ height: '280px', width: '100%' }}>
                  <Doughnut 
                    data={doughnutChartData} 
                    options={{ 
                      responsive: true, 
                      maintainAspectRatio: false,
                      plugins: {
                        legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 10 } }
                      },
                      cutout: '70%'
                    }} 
                  />
                </div>
              ) : (
                <div className="text-center text-muted p-4">
                  <i className="bi bi-pie-chart-fill display-4 d-block mb-2 opacity-25"></i>
                  <p className="small fw-bold">Sin datos para categorías</p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>

        {/* ORDERS CHART */}
        <Col lg={12} className="mb-4">
          <Card className="shadow-sm border-0">
            <Card.Header className="bg-white py-3 border-0">
              <h5 className="mb-0 fw-bold"><i className="bi bi-box-seam me-2"></i>Volumen de Pedidos</h5>
            </Card.Header>
            <Card.Body>
              <div style={{ height: '300px' }}>
                <Bar 
                  data={ordersChartData} 
                  options={{ 
                    responsive: true, 
                    maintainAspectRatio: false,
                    plugins: {
                      legend: { display: false }
                    },
                    scales: {
                      y: { beginAtZero: true, ticks: { stepSize: 1 } }
                    }
                  }} 
                />
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
