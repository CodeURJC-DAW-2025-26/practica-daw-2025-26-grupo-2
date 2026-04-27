import "./order-detail.css";
import { useActionState, useEffect, useState } from "react";
import { useNavigate, Link } from "react-router";
import type { Route } from "./+types/order-edit";
import { Container, Row, Col, Form, Button, Alert, Spinner, Table, Badge } from "react-bootstrap";
import { getOrder, updateOrder } from "~/services/orders-service";
import { requireAuth, requireOwnerOrRole } from "~/services/auth-service";

export async function clientLoader({ request, params }: Route.ClientLoaderArgs) {
  const user = await requireAuth(request);

  const orderId = Number(params.id);
  const order = await getOrder(orderId);

  requireOwnerOrRole(user, order.user.id, "ADMIN");

  return { order };
}

export default function OrderEdit({ loaderData, params }: Route.ComponentProps) {
  const { order } = loaderData;
  const orderId = Number(params.id);
  const navigate = useNavigate();
  const [validated, setValidated] = useState(false);

  async function handleSubmit(
    prevState: { success: boolean; error: string | null },
    formData: FormData
  ) {
    const deliveryAddress = formData.get("deliveryAddress") as string;
    const deliveryDate = formData.get("deliveryDate") as string;
    const deliveryNote = formData.get("deliveryNote") as string;
    const completed = order.completed;

    try {
      await updateOrder(orderId, deliveryAddress, deliveryNote, deliveryDate, completed);
      return { success: true, error: null };
    } catch (err: any) {
      return { success: false, error: err.message || "Error al actualizar el pedido" };
    }
  }

  const [state, formAction, isPending] = useActionState(handleSubmit, {
    success: false,
    error: null,
  });

  useEffect(() => {
    if (state.success) {
      navigate(`/orders/${orderId}`);
    }
  }, [state.success]);

  function formatDate(dateString: string): string {
    if (!dateString) return "—";
    return new Date(dateString).toLocaleDateString("es-ES");
  }

  return (
    <Container className="container-main mt-5 mb-5">

      <Row className="mb-4">
        <Col>
          <Link to={`/orders/${orderId}`} className="btn btn-secondary text-dark">
            <i className="bi bi-arrow-left" /> Volver
          </Link>
        </Col>
      </Row>

      <h2 className="text-center mb-4">Editar Pedido #{order.id}</h2>

      <Form
        action={formAction}
        noValidate
        validated={validated}
        onSubmit={(e) => {
          if (!e.currentTarget.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
          }
          setValidated(true);
        }}
      >
        {state.error && <Alert variant="danger">{state.error}</Alert>}

        <Row>
          <Col md={12}>
            <h4 className="mb-3">Información del Pedido</h4>
            <Table bordered className="align-middle">
              <tbody>
                <tr>
                  <th style={{ width: "220px" }}>Fecha del Pedido</th>
                  <td>{formatDate(order.creationDate)}</td>
                </tr>
                <tr>
                  <th>Precio Total</th>
                  <td>{order.totalPrice}€</td>
                </tr>
                <tr>
                  <th>Gastos de Envío</th>
                  <td>{order.shippingCost != null ? `${order.shippingCost}€` : "—"}</td>
                </tr>
                <tr>
                  <th>Estado del Pedido</th>
                  <td>
                    <Badge bg="warning" text="dark">
                      {order.completed ? "COMPLETADO" : "EN CURSO"}
                    </Badge>
                  </td>
                </tr>
              </tbody>
            </Table>
          </Col>
        </Row>

        <Row className="mt-4">
          <Col md={12}>
            <h4 className="mb-3">Información de Entrega</h4>
            <Table bordered className="align-middle">
              <tbody>
                <tr>
                  <th style={{ width: "220px" }}>Dirección de Entrega</th>
                  <td>
                    <Form.Control
                      type="text"
                      name="deliveryAddress"
                      defaultValue={order.deliveryAddress || ""}
                      required
                      minLength={5}
                    />
                    <Form.Control.Feedback type="invalid">
                      La dirección de entrega no puede estar vacía
                    </Form.Control.Feedback>
                  </td>
                </tr>
                <tr>
                  <th>Fecha de Entrega Preferida</th>
                  <td>
                    <Form.Control
                      type="date"
                      name="deliveryDate"
                      defaultValue={order.deliveryDate || ""}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      La fecha de entrega no puede estar vacía
                    </Form.Control.Feedback>
                  </td>
                </tr>
                <tr>
                  <th>Notas de Entrega</th>
                  <td>
                    <Form.Control
                      as="textarea"
                      name="deliveryNote"
                      defaultValue={order.deliveryNote || ""}
                      rows={3}
                    />
                  </td>
                </tr>
              </tbody>
            </Table>
          </Col>
        </Row>

        <div className="text-center mt-4">
          <Button
            variant="success"
            type="submit"
            disabled={isPending}
            className="me-2"
          >
            {isPending ? (
              <><Spinner size="sm" className="me-2" />Guardando...</>
            ) : (
              <>Guardar Cambios <i className="bi bi-check-circle" /></>
            )}
          </Button>
          <Link to={`/orders/${orderId}`} className="btn btn-danger">
            Cancelar <i className="bi bi-x-lg" />
          </Link>
        </div>
      </Form>
    </Container>
  );
}
