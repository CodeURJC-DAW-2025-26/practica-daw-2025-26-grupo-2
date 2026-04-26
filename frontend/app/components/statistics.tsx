import { useEffect, useState } from "react";
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
  BarElement 
} from 'chart.js';
import { Line, Bar } from 'react-chartjs-2';
import { Card, Row, Col, Form } from "react-bootstrap";
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
  initialIncome: number[];
  initialOrders: number[];
  initialLabels: number[];
  currentPeriod: string;
}

export default function Statistics({ initialIncome, initialOrders, currentPeriod }: StatisticsProps) {
  const [searchParams, setSearchParams] = useSearchParams();
  
  // Usamos los datos iniciales que vienen del loader
  const labels = initialIncome.map((_, i) => `${currentPeriod === 'month' ? 'Mes' : (currentPeriod === 'year' ? 'Año' : 'Día')} ${i + 1}`);

  const incomeChartData = {
    labels,
    datasets: [
      {
        label: 'Ingresos (€)',
        data: initialIncome,
        borderColor: 'rgb(107, 79, 63)',
        backgroundColor: 'rgba(107, 79, 63, 0.5)',
        tension: 0.3,
        fill: true,
      },
    ],
  };

  const ordersChartData = {
    labels,
    datasets: [
      {
        label: 'Número de Pedidos',
        data: initialOrders,
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
        borderColor: 'rgb(54, 162, 235)',
        borderWidth: 1,
      },
    ],
  };

  const handlePeriodChange = (e: React.ChangeEvent<any>) => {
    setSearchParams({ period: e.target.value });
  };

  return (
    <div className="statistics-container">
      <Row className="mb-4">
        <Col md={12} className="d-flex justify-content-end mb-3">
          <div className="d-flex align-items-center gap-2">
            <span className="fw-bold">Periodo:</span>
            <Form.Select 
              size="sm" 
              style={{ width: '150px' }} 
              value={currentPeriod}
              onChange={handlePeriodChange}
            >
              <option value="day">Días</option>
              <option value="month">Meses</option>
              <option value="year">Años</option>
            </Form.Select>
          </div>
        </Col>

        {/* GRÁFICA DE INGRESOS */}
        <Col lg={6} className="mb-4">
          <Card className="shadow-sm h-100">
            <Card.Header className="bg-white py-3">
              <h5 className="mb-0 fw-bold">Histórico de Ingresos</h5>
            </Card.Header>
            <Card.Body>
              <div style={{ height: '350px' }}>
                <Line 
                  data={incomeChartData} 
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

        {/* GRÁFICA DE PEDIDOS */}
        <Col lg={6} className="mb-4">
          <Card className="shadow-sm h-100">
            <Card.Header className="bg-white py-3">
              <h5 className="mb-0 fw-bold">Volumen de Pedidos</h5>
            </Card.Header>
            <Card.Body>
              <div style={{ height: '350px' }}>
                <Bar 
                  data={ordersChartData} 
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
