import "./cart.css";
import { useState, useActionState } from "react";
import { useNavigate, redirect } from "react-router";
import type { Route } from "./+types/cart";
import { Container, Row, Col, Button, Form, Alert, Spinner, Badge } from "react-bootstrap";
import { getOrder, updateOrder } from "~/services/orders-service";
import { getOrderItems, disableOrderItem } from "~/services/orderItems-service";
import { useUserStore } from "~/stores/user-store";
import type OrderExtendedDTO from "~/dtos/OrderExtendedDTO";
import type OrderItemBasicDTO from "~/dtos/OrderItemBasicDTO";

const PAGE_SIZE = 10;

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  await useUserStore.getState().loadLoggedUser();
  const { user } = useUserStore.getState();

  if (!user) {
    throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
  }

  if (!user?.cart?.id) {
    return { order: null, orderItems: [], user, hasMore: false };
  }

  const cartId = user.cart.id;
  const order = await getOrder(cartId);
  const orderItems = await getOrderItems(cartId, 0, PAGE_SIZE);

  return {
    order,
    orderItems,
    user,
    hasMore: orderItems.length === PAGE_SIZE,
  };
}

export default function Cart({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const { order: initialOrder, user: loadedUser } = loaderData;

  const user = loadedUser;
  const [order, setOrder] = useState<OrderExtendedDTO | null>(initialOrder);
  const [orderItems, setOrderItems] = useState<OrderItemBasicDTO[]>(loaderData.orderItems);
  const [hasMore, setHasMore] = useState(loaderData.hasMore);
  const [page, setPage] = useState(1);
  const [loadingMore, setLoadingMore] = useState(false);

  function handleCheckoutSubmit(
    prevState: { success: boolean; error: string | null } | null,
    formData: FormData
  ) {
    const deliveryAddress = formData.get("deliveryAddress") as string;
    const deliveryDate = formData.get("deliveryDate") as string;
    const deliveryNote = formData.get("deliveryNote") as string;

    const promise = (async () => {
      try {
        await updateOrder(
          order!.id,
          deliveryAddress,
          deliveryNote,
          deliveryDate,
          true // completed = true => procesa el pedido
        );
        navigate(`/orders?userId=${user?.id}`);
        return { success: true, error: null };
      } catch (error: any) {
        console.error("Error al procesar el pedido:", error);
        navigate(`/error?message=${encodeURIComponent(error?.message || "Error al procesar el pedido")}`);
        return { success: false, error: null };
      }
    })();

    return promise;
  }

  const [checkoutState, checkoutAction, isPendingCheckout] = useActionState(
    handleCheckoutSubmit,
    null
  );

  async function handleRemoveItem(itemId: number) {
    try {
      await disableOrderItem(order!.id, itemId);
      const newItems = orderItems.filter((i) => i.id !== itemId);
      setOrderItems(newItems);
      // Recargar el pedido para actualizar totales
      const updatedOrder = await getOrder(order!.id);
      setOrder(updatedOrder);
    } catch (error: any) {
      navigate(`/error?message=${encodeURIComponent(error?.message || "Error al eliminar el producto")}`);
    }
  }

  async function handleLoadMore() {
    setLoadingMore(true);
    try {
      const more = await getOrderItems(order!.id, page, PAGE_SIZE);
      setOrderItems((prev) => [...prev, ...more]);
      setHasMore(more.length === PAGE_SIZE);
      setPage(page + 1);
    } finally {
      setLoadingMore(false);
    }
  }

  // Empty cart
  if (!order) {
    return (
      <Container className="my-4">
        <div className="text-center">
          <h2 className="mb-3">Tu carrito de compras está vacío</h2>
          <p className="text-muted mb-4">Explora nuestros productos y añade algo a tu carrito.</p>
          <Button variant="primary" onClick={() => navigate("/")}>
            <i className="bi bi-shop"></i> Ir a la tienda
          </Button>
        </div>
      </Container>
    );
  }

  return (
    <Container className="my-4">
      <section className="cart-shell">
        <div className="cart-header mb-4">
          <h2 className="mb-0">Tu carrito de compras</h2>
          <span className="text-muted">Revisa tus productos y completa tu compra</span>
        </div>

        <div id="cart-items">
          {orderItems.length === 0 ? (
            <Alert variant="info">No hay productos en el carrito.</Alert>
          ) : (
            orderItems.map((item) => (
              <div key={item.id} className="cart-item">
                <div className="cart-item-info d-flex align-items-center gap-3">
                  <img
                    src={
                      item.garment.image
                        ? `/api/v1/images/${item.garment.image.id}/media`
                        : "/placeholder.png"
                    }
                    alt={item.garment.name}
                    className="cart-item-image"
                  />
                  <div>
                    <h5 className="mb-1">{item.garment.name}</h5>
                    <p className="text-muted mb-0">Ref: {item.garment.reference}</p>
                    <p className="text-muted mb-0">Talla: {item.size}</p>
                    <p className="text-muted mb-0">Precio unitario: {item.garment.price}€</p>
                  </div>
                </div>
                <div className="cart-item-controls">
                  <span className="fw-bold">
                    Subtotal: {(item.garment.price * item.quantity).toFixed(2)}€
                  </span>
                  <div className="quantity-controls">
                    <input
                      disabled
                      type="number"
                      className="form-control form-control-quantity"
                      value={item.quantity}
                      min={1}
                      max={50}
                      readOnly
                    />
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => handleRemoveItem(item.id)}
                    >
                      <i className="bi bi-x-lg"></i> Eliminar
                    </Button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {hasMore && (
          <div className="text-center mt-4">
            <Button variant="primary" onClick={handleLoadMore} disabled={loadingMore}>
              {loadingMore ? (
                <><Spinner size="sm" className="me-2" />Cargando...</>
              ) : (
                <>Cargar más <i className="bi bi-plus-circle"></i></>
              )}
            </Button>
          </div>
        )}

        <hr className="my-4" />

        <Form action={checkoutAction}>
          {checkoutState?.error && (
            <Alert variant="danger">{checkoutState.error}</Alert>
          )}

          <div className="delivery-info mb-4">
            <h3 className="mb-3">Preferencias de entrega</h3>

            <Form.Group className="mb-3">
              <Form.Label htmlFor="deliveryAddress">Dirección de entrega</Form.Label>
              <Form.Control
                type="text"
                id="deliveryAddress"
                name="deliveryAddress"
                placeholder="Calle, Ciudad, CP"
                defaultValue={order.deliveryAddress || ""}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label htmlFor="deliveryDate">Fecha de entrega preferida</Form.Label>
              <Form.Control
                type="date"
                id="deliveryDate"
                name="deliveryDate"
                defaultValue={order.deliveryDate || ""}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label htmlFor="deliveryNote">Notas de entrega</Form.Label>
              <Form.Control
                as="textarea"
                id="deliveryNote"
                name="deliveryNote"
                rows={3}
                placeholder="Instrucciones especiales, etc."
                defaultValue={order.deliveryNote || ""}
              />
            </Form.Group>
          </div>

          <hr className="my-4" />

          <div className="cart-summary mb-4">
            <h3 className="mb-3">Resumen del carrito</h3>
            <div className="d-flex justify-content-between">
              <span className="text-muted">Total productos</span>
              <span>{order.subtotal}€</span>
            </div>
            <div className="d-flex justify-content-between">
              <span className="text-muted">Envío</span>
              <span>{order.shippingCost}€</span>
            </div>
            <div className="d-flex justify-content-between mb-3">
              <span className="text-muted">Total a pagar</span>
              <span className="fw-bold">{order.totalPrice}€</span>
            </div>
          </div>

          <div className="d-flex justify-content-between align-items-center">
            <Button variant="secondary" onClick={() => navigate("/")}>
              <i className="bi bi-arrow-left-circle"></i> Continuar comprando
            </Button>
            <span className="text-muted small">ID del pedido: {order.id}</span>
            <Button
              variant="success"
              type="submit"
              disabled={isPendingCheckout || orderItems.length === 0}
            >
              {isPendingCheckout ? (
                <><Spinner size="sm" className="me-2" />Procesando...</>
              ) : (
                <>Comprar <i className="bi bi-credit-card"></i></>
              )}
            </Button>
          </div>
        </Form>
      </section>
    </Container>
  );
}
