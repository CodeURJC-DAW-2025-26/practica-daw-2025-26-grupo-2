import "./order-list.css";
import { useState } from "react";
import type { Route } from "./+types/order-list";
import { Container, Table, Button, Spinner, Alert } from "react-bootstrap";
import { getOrders, getUserOrders, deleteOrder } from "~/services/orders-service";
import OrderCard from "~/components/order-card";
import { useUserStore } from "~/stores/user-store";
import { requireAuth, requireRole } from "~/services/auth-service";
import type OrderBasicDTO from "~/dtos/OrderBasicDTO";

const PAGE_SIZE = 10;

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  // We can't use the hook here, so we directly access the store's state and methods
  // TODO: Ask about a better way to handle this, maybe with a custom hook that can be used in loaders?
  // TODO: Ask about the way to handle navigation back when not authorized or authenticated

  requireAuth();
  
  requireRole("ADMIN");

  const userId = url.searchParams.get("userId");

  if (userId) {
    const orders = await getUserOrders(Number(userId), 0, PAGE_SIZE);
    return { orders, userId: Number(userId), hasMore: orders.length === PAGE_SIZE };
  }

  const orders = await getOrders(0, PAGE_SIZE);
  return { orders, userId: null, hasMore: orders.length === PAGE_SIZE };
}

export default function OrdersList({ loaderData }: Route.ComponentProps) {

  const [orders, setOrders] = useState<OrderBasicDTO[]>(loaderData.orders);
  const [hasMore, setHasMore] = useState(loaderData.hasMore);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);

  const userId = loaderData.userId;
  const isUserOrders = userId !== null;
  const isAdmin = !isUserOrders;
  const titulo = isUserOrders ? "Mis Pedidos" : "Gestión de Pedidos";

  async function handleLoadMore() {
    setLoading(true);
    try {
      const more = isUserOrders
        ? await getUserOrders(userId!, page, PAGE_SIZE)
        : await getOrders(page, PAGE_SIZE);
      setOrders((prev) => [...prev, ...more]);
      setHasMore(more.length === PAGE_SIZE);
      setPage(page + 1);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id: number) {
    await deleteOrder(id);
    setOrders(orders.filter((item) => item.id !== id));
  }

  return (
    <Container className="container-main mt-5 mb-5">
      <h2 className="text-center mb-4">{titulo}</h2>

      <div className="table-responsive">
        <Table bordered striped className="order-list-table">
          <thead className="table-light">
            <tr>
              <th>ID del Pedido</th>
              {!isUserOrders && <th>Correo Usuario</th>}
              <th>Fecha del Pedido</th>
              <th>Precio Total</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0 ? (
              <tr>
                <td colSpan={isUserOrders ? 4 : 5}>
                  <Alert variant="info" className="mb-0">No hay pedidos disponibles</Alert>
                </td>
              </tr>
            ) : (
              orders.map((order: OrderBasicDTO) => (
                <OrderCard
                  key={order.id}
                  order={order}
                  isAdmin={!isUserOrders && isAdmin}
                  onDelete={handleDelete}
                />
              ))
            )}
          </tbody>
        </Table>
      </div>

      {hasMore && (
        <div className="text-center mt-4">
          <Button
            variant="primary"
            className="load-more-btn"
            onClick={handleLoadMore}
            disabled={loading}
          >
            {loading ? (
              <><Spinner size="sm" className="me-2" />Cargando...</>
            ) : (
              <>Cargar más <i className="bi bi-arrow-down-circle" /></>
            )}
          </Button>
        </div>
      )}
    </Container>
  );
}