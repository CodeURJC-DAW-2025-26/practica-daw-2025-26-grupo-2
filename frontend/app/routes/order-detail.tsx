import "./order-detail.css";
import { useState, useActionState, useEffect } from "react";
import { useNavigate } from "react-router";
import type { Route } from "./+types/order-detail";
import { Container, Row, Col, Button, Form, Table, Alert, Spinner, Badge } from "react-bootstrap";
import { getOrder, updateOrder } from "~/services/orders-service";
import { getOrderItems } from "~/services/orderItems-service";
import { requireAuth, requireOwnerOrRole } from "~/services/auth-service";
import type OrderExtendedDTO from "~/dtos/OrderExtendedDTO";
import type OrderItemBasicDTO from "~/dtos/OrderItemBasicDTO";

const PAGE_SIZE = 10;

export async function clientLoader({ params, request }: Route.ClientLoaderArgs) {
  const orderId = Number(params.id);
  const url = new URL(request.url);
  const isEditing = url.searchParams.get("edit") === "true";

  const user = await requireAuth(request);

  const order = await getOrder(orderId);
  const orderItems = await getOrderItems(orderId, 0, PAGE_SIZE);

  requireOwnerOrRole(user, order.user.id, "ADMIN");

  const isAdmin = user?.roles?.includes("ADMIN") ?? false;
  const isOwner = user?.id === order.user.id;

  return { 
    order, 
    orderItems,
    user,
    isEditing,
    hasMore: orderItems.length === PAGE_SIZE 
  };
}

export default function OrderDetail({ loaderData, params }: Route.ComponentProps) {
  const navigate = useNavigate();
  const orderId = Number(params.id);
  const { order: initialOrder, user: loadedUser, isEditing: initialIsEditing } = loaderData;
  
  const user = loadedUser;
  const isLogged = !!user;
  const isAdmin = user?.roles?.includes("ADMIN") ?? false;
  const isOwner = user?.id === initialOrder.user.id;

  const [order, setOrder] = useState<OrderExtendedDTO>(initialOrder);
  const [orderItems, setOrderItems] = useState<OrderItemBasicDTO[]>(loaderData.orderItems);
  const [hasMore, setHasMore] = useState(loaderData.hasMore);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [isEditing, setIsEditing] = useState(initialIsEditing);

  function handleOrderSubmit(
    prevState: { success: boolean; error: string | null } | null,
    formData: FormData
  ) {
    const totalPrice = Number(formData.get("totalPrice"));
    const shippingCost = Number(formData.get("shippingCost"));
    const deliveryAddress = formData.get("deliveryAddress") as string;
    const deliveryDate = formData.get("deliveryDate") as string;
    const deliveryNote = formData.get("deliveryNote") as string;

    const promise = (async () => {
      try {
        const updatedOrder = await updateOrder(
          orderId, 
          deliveryAddress, 
          deliveryNote, 
          deliveryDate, 
          order.completed
        );
        
        setOrder(updatedOrder);
        setIsEditing(false);
        navigate(`/orders/${orderId}`);
        
        return { success: true, error: null };
      } catch (error: any) {
        navigate(`/error?message=${encodeURIComponent(error?.message || "Error al actualizar el pedido")}`);
        return { success: false, error: null };
      }
    })();

    return promise;
  }

  const [orderState, orderAction, isPendingOrder] = useActionState(handleOrderSubmit, null);

  async function handleLoadMore() {
    setLoading(true);
    try {
      const more = await getOrderItems(orderId, page, PAGE_SIZE);
      setOrderItems((prev) => [...prev, ...more]);
      setHasMore(more.length === PAGE_SIZE);
      setPage(page + 1);
    } finally {
      setLoading(false);
    }
  }

  function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES');
  }

  function calculateSubtotal(item: OrderItemBasicDTO): number {
    return item.garment.price * item.quantity;
  }

  return (
    <Container className="container-main mt-5 mb-5">
      <Row className="mb-4">
        <Col md={12}>
          <Button variant="secondary" className="text-dark" onClick={() => navigate("/orders")}>
            <i className="bi bi-arrow-left"></i> Volver
          </Button>
        </Col>
      </Row>

      <h2 className="text-center mb-4">Detalle del Pedido #{order.id}</h2>

      <Form action={orderAction}>
        {orderState?.error && (
          <Alert variant="danger">{orderState.error}</Alert>
        )}

        <Row>
          <Col md={12}>
            <h4 className="mb-3">Información del Pedido</h4>
            <Table bordered className="align-middle">
              <tbody>
                <tr>
                  <th>Fecha del Pedido</th>
                  <td>{formatDate(order.creationDate)}</td>
                </tr>
                <tr>
                  <th>Precio Total</th>
                  <td>
                    {isEditing ? (
                      <Form.Control
                        type="number"
                        step="0.01"
                        name="totalPrice"
                        defaultValue={order.totalPrice}
                        min="0"
                        max="1000000"
                        required
                        disabled
                      />
                    ) : (
                      `${order.totalPrice}€`
                    )}
                  </td>
                </tr>
                <tr>
                  <th>Gastos de Envío</th>
                  <td>
                    {isEditing ? (
                      <Form.Control
                        type="number"
                        step="0.01"
                        name="shippingCost"
                        defaultValue={order.shippingCost}
                        min="0"
                        max="1000000"
                        required
                        disabled
                      />
                    ) : (
                      `${order.shippingCost}€`
                    )}
                  </td>
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
                  <th>Dirección de Entrega</th>
                  <td>
                    {isEditing ? (
                      <Form.Control
                        type="text"
                        name="deliveryAddress"
                        defaultValue={order.deliveryAddress}
                        required
                      />
                    ) : (
                      order.deliveryAddress || "No especificada"
                    )}
                  </td>
                </tr>
                <tr>
                  <th>Fecha de Entrega Preferida</th>
                  <td>
                    {isEditing ? (
                      <Form.Control
                        as="textarea"
                        name="deliveryDate"
                        defaultValue={order.deliveryDate}
                      />
                    ) : (
                      order.deliveryDate || "No especificada"
                    )}
                  </td>
                </tr>
                <tr>
                  <th>Notas de Entrega</th>
                  <td>
                    {isEditing ? (
                      <Form.Control
                        as="textarea"
                        name="deliveryNote"
                        defaultValue={order.deliveryNote}
                      />
                    ) : (
                      order.deliveryNote || "No especificadas"
                    )}
                  </td>
                </tr>
              </tbody>
            </Table>
          </Col>
        </Row>

        {isEditing && (
          <div className="text-center mt-3">
            <Button 
              variant="success" 
              type="submit"
              disabled={isPendingOrder}
              className="me-2"
            >
              {isPendingOrder ? (
                <><Spinner size="sm" className="me-2" />Guardando...</>
              ) : (
                <>Guardar Cambios <i className="bi bi-check-circle"></i></>
              )}
            </Button>
            <Button 
              variant="danger" 
              onClick={() => {
                setIsEditing(false);
                navigate(`/orders/${orderId}`);
              }}
            >
              Cancelar <i className="bi bi-x-lg"></i>
            </Button>
          </div>
        )}
      </Form>

      <Row className="mt-4">
        <Col md={12}>
          <h4 className="mb-3">Productos del Pedido</h4>
          <div className="table-responsive">
            <Table bordered>
              <thead className="table-light">
                <tr>
                  <th>Producto</th>
                  <th>Imagen</th>
                  <th>Talla</th>
                  <th>Cantidad</th>
                  <th>Precio por Unidad</th>
                  <th>Subtotal</th>
                </tr>
              </thead>
              <tbody>
                {orderItems.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="text-center">
                      No hay productos en este pedido
                    </td>
                  </tr>
                ) : (
                  orderItems.map((item) => (
                    <tr key={item.id}>
                      <td>
                        {item.garment.name}
                        <br />
                        <small className="text-muted">Ref: {item.garment.reference}</small>
                      </td>
                      <td>
                        <img
                          src={
                            item.garment.image
                              ? `/api/v1/images/${item.garment.image.id}/media`
                              : "/placeholder.png"
                          }
                          className="img-fluid"
                          style={{ width: "50px" }}
                          alt={item.garment.name}
                        />
                      </td>
                      <td>{item.size}</td>
                      <td>{item.quantity}</td>
                      <td>{item.garment.price}€</td>
                      <td>{calculateSubtotal(item).toFixed(2)}€</td>
                    </tr>
                  ))
                )}
              </tbody>
            </Table>
          </div>
        </Col>
      </Row>

      {hasMore && (
        <div className="text-center mt-4">
          <Button
            variant="primary"
            onClick={handleLoadMore}
            disabled={loading}
          >
            {loading ? (
              <><Spinner size="sm" className="me-2" />Cargando...</>
            ) : (
              <>Cargar más <i className="bi bi-plus-circle"></i></>
            )}
          </Button>
        </div>
      )}

      {!isEditing && order.completed && (
        <div className="text-center mt-4">
          <a
            href={`/api/v1/orders/${order.id}/invoice`}
            className="btn btn-info"
            target="_blank"
            rel="noreferrer"
          >
            Generar Factura <i className="bi bi-file-earmark-pdf"></i>
          </a>
        </div>
      )}
    </Container>
  );
}