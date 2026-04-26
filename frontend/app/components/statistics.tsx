import { useEffect, useState } from "react";
import { 
  Chart as ChartJS, 
  CategoryScale, 
  LinearScale, 
  PointElement, 
  LineElement, 
  Title, 
  Tooltip, 
  Legend, 
  BarElement 
} from 'chart.js';
import { Line, Bar } from 'react-chartjs-2';
import { getIncomeStatistics } from "~/services/statistics-service";
import { Card, Spinner, Row, Col, Form } from "react-bootstrap";
import "./statistics.css";

// Registrar componentes de Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
);

interface StatisticsProps {
  userId: number;
}

export default function Statistics({ userId }: StatisticsProps) {
  const [incomeData, setIncomeData] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [period, setPeriod] = useState("month");

  useEffect(() => {
    async function loadData() {
      setLoading(true);
      try {
        const data = await getIncomeStatistics(period, 12);
        setIncomeData(data);
      } catch (error) {
        console.error("Error loading statistics:", error);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [period]);

  const chartData = {
    labels: incomeData.map((_, i) => `${period === 'month' ? 'Mes' : 'Día'} ${i + 1}`),
    datasets: [
      {
        label: 'Ingresos (€)',
        data: incomeData,
        borderColor: 'rgb(107, 79, 63)',
        backgroundColor: 'rgba(107, 79, 63, 0.5)',
        tension: 0.3,
      },
    ],
  };

  if (loading && incomeData.length === 0) {
    return (
      <div className="text-center p-5">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2">Cargando estadísticas...</p>
      </div>
    );
  }

  return (
    <div className="statistics-container">
      <Row className="mb-4">
        <Col md={12}>
          <Card className="shadow-sm">
            <Card.Header className="bg-white d-flex justify-content-between align-items-center">
              <h5 className="mb-0">Histórico de Ingresos</h5>
              <Form.Select 
                size="sm" 
                style={{ width: '150px' }} 
                value={period}
                onChange={(e) => setPeriod(e.target.value)}
              >
                <option value="day">Últimos días</option>
                <option value="month">Últimos meses</option>
                <option value="year">Últimos años</option>
              </Form.Select>
            </Card.Header>
            <Card.Body>
              <div style={{ height: '400px' }}>
                <Line 
                  data={chartData} 
                  options={{ 
                    responsive: true, 
                    maintainAspectRatio: false,
                    plugins: {
                      legend: { position: 'top' as const },
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
