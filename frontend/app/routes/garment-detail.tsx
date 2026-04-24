import "./garment-detail.css";
import { useState, useActionState, useEffect, useRef } from "react";
import { useNavigate, redirect } from "react-router";
import type { Route } from "./+types/garment-detail";
import { Container, Row, Col, Button, Form, Accordion, Alert, Spinner, Card } from "react-bootstrap";
import { getGarment, disableGarment } from "~/services/garments-service";
import { getOpinions, addOpinion, updateOpinion, deleteOpinion } from "~/services/opinions-service";
import { addOrderItem } from "~/services/orderItems-service";
import { getOrCreateCart } from "~/services/orders-service";
import { useUserStore } from "~/stores/user-store";
import type GarmentExtendedDTO from "~/dtos/GarmentExtendedDTO";
import type OpinionExtendedDTO from "~/dtos/OpinionExtendedDTO";

const PAGE_SIZE = 10;

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  const garmentId = Number(params.id);
  
  await useUserStore.getState().loadLoggedUser();
  const { user } = useUserStore.getState();

  const garment = await getGarment(garmentId);
  const opinions = await getOpinions(garmentId, 0, PAGE_SIZE);

  return { 
    garment, 
    opinions,
    user,
    hasMore: opinions.length === PAGE_SIZE 
  };
}

export default function GarmentDetail({ loaderData, params }: Route.ComponentProps) {
  const navigate = useNavigate();
  const garmentId = Number(params.id);
  const { garment, user: loadedUser } = loaderData;
  
  // Use user from loader data instead of store to ensure it's available immediately
  const user = loadedUser;
  const isLogged = !!user;
  const isAdmin = user?.roles?.includes("ADMIN") ?? false;

  const [opinions, setOpinions] = useState<OpinionExtendedDTO[]>(loaderData.opinions);
  const [hasMore, setHasMore] = useState(loaderData.hasMore);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [editingOpinion, setEditingOpinion] = useState<OpinionExtendedDTO | null>(null);
  const formRef = useRef<HTMLDivElement>(null);

  async function handleAddToCart(formData: FormData) {
    try {
      const size = formData.get("size") as string;
      const quantity = Number(formData.get("quantity"));

      // Create or get cart, then add item to cart
      const cart = await getOrCreateCart();
      await addOrderItem(cart.id, garmentId, quantity, size);
      navigate("/cart");
    } catch (error) {
      alert("Error al añadir al carrito");
    }
  }

  function handleOpinionSubmit(
    prevState: { success: boolean; error: string | null } | null,
    formData: FormData
  ) {
    const comment = formData.get("comment") as string;
    const rating = Number(formData.get("rating"));
    const opinionId = formData.get("opinionId") as string;

    const promise = (async () => {
      try {
        let savedOpinion: OpinionExtendedDTO;
        
        if (opinionId) {
          // Update existing opinion
          savedOpinion = await updateOpinion(garmentId, Number(opinionId), comment, rating);
        } else {
          // Add new opinion
          savedOpinion = await addOpinion(garmentId, comment, rating);
        }

        setEditingOpinion(null);
        
        // Refresh opinions list
        const newOpinions = await getOpinions(garmentId, 0, PAGE_SIZE);
        setOpinions(newOpinions);
        setPage(1);
        setHasMore(newOpinions.length === PAGE_SIZE);
        
        return { success: true, error: null };
      } catch (error) {
        console.error("Error al guardar opinión:", error);
        return { success: false, error: "Error al guardar la opinión" };
      }
    })();

    return promise;
  }

  const [opinionState, opinionAction, isPendingOpinion] = useActionState(handleOpinionSubmit, null);

  // Reset form when action succeeds
  useEffect(() => {
    if (opinionState?.success) {
      setEditingOpinion(null);
    }
  }, [opinionState]);

  async function handleDeleteOpinion(opinionId: number) {
    try {
      await deleteOpinion(garmentId, opinionId);
      setEditingOpinion(null);
      
      // Refresh opinions list
      const newOpinions = await getOpinions(garmentId, 0, PAGE_SIZE);
      setOpinions(newOpinions);
      setPage(1);
      setHasMore(newOpinions.length === PAGE_SIZE);
    } catch (error) {
      alert("Error al eliminar la opinión");
    }
  }

  async function handleDeleteGarment() {
    try {
      await disableGarment(garmentId);
      alert("Prenda eliminada exitosamente");
      navigate("/");
    } catch (error) {
      alert("Error al eliminar la prenda");
    }
  }

  async function handleLoadMore() {
    setLoading(true);
    try {
      const more = await getOpinions(garmentId, page, PAGE_SIZE);
      setOpinions((prev) => [...prev, ...more]);
      setHasMore(more.length === PAGE_SIZE);
      setPage(page + 1);
    } finally {
      setLoading(false);
    }
  }

  function canEditOpinion(opinion: OpinionExtendedDTO): boolean {
    if (!isLogged) return false;
    if (isAdmin) return true;
    return Number(opinion.user?.id) === Number(user?.id);
  }

  function handleEditOpinion(opinion: OpinionExtendedDTO) {
    setEditingOpinion(opinion);
    // Scroll to form
    formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  function renderStars(rating: number) {
    const filled = "★".repeat(rating);
    const empty = "☆".repeat(5 - rating);
    return filled + empty;
  }

  return (
    <Container className="container-main mt-4 mb-5">
      <Row className="align-items-center mb-4">
        <Col xs="auto">
          <Button variant="secondary" className="fw-bold" onClick={() => navigate("/")}>
            <i className="bi bi-arrow-left"></i> Volver
          </Button>
        </Col>
        <Col>
          <h2 className="title mb-0">{garment.name}</h2>
        </Col>
        <Col xs="auto">
          <p className="text-muted small mb-0">Ref: {garment.reference}</p>
        </Col>
      </Row>

      <Row className="container-attributes-detail">
        <Col md={6}>
          <aside className="aside">
            <img 
              src={garment.image ? `/api/v1/images/${garment.image.id}/media` : "/placeholder.png"} 
              className="rounded w-100" 
              alt={garment.name}
            />
          </aside>
        </Col>

        <Col md={6}>
          <div className="container-price-item-detail">
            <h3 className="fw-bold">{garment.price}€</h3>
          </div>

          {isLogged && (
            <div className="mt-4">
              <Form onSubmit={(e) => { 
                e.preventDefault(); 
                handleAddToCart(new FormData(e.currentTarget)); 
            }}>
                <Form.Group className="mb-4">
                  <Form.Label><b>Talla</b></Form.Label>
                  <Form.Select name="size" required>
                    <option value="">Talla de la prenda</option>
                    <option value="S">S</option>
                    <option value="M">M</option>
                    <option value="L">L</option>
                    <option value="XL">XL</option>
                  </Form.Select>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label><b>Cantidad</b></Form.Label>
                  <Form.Control
                    type="number"
                    name="quantity"
                    min="1"
                    max="100"
                    defaultValue="1"
                    placeholder="Cantidad de prendas"
                    required
                  />
                </Form.Group>

                <div className="d-grid gap-2 mb-4">
                  <Button variant="success" className="py-2 fw-bold text-dark" type="submit">
                    Añadir al carrito <i className="bi bi-cart-plus"></i>
                  </Button>
                </div>
              </Form>

              {isAdmin && (
                <>
                  <hr />
                  <div className="d-flex gap-2">
                    <Button 
                      variant="warning" 
                      className="flex-grow-1 text-dark"
                      onClick={() => navigate(`/garment/${garmentId}/edit`)}
                    >
                      Editar <i className="bi bi-pencil-square"></i>
                    </Button>
                    <Button 
                      variant="danger" 
                      className="flex-grow-1 text-dark"
                      onClick={handleDeleteGarment}
                    >
                      Borrar <i className="bi bi-x-lg"></i>
                    </Button>
                  </div>
                </>
              )}
            </div>
          )}
        </Col>
      </Row>

      <Row className="mt-5">
        <Col xs={12}>
          <h3 className="details-item-title">Detalles del producto</h3>
          <Accordion>
            <Accordion.Item eventKey="0">
              <Accordion.Header>Categoría</Accordion.Header>
              <Accordion.Body>{garment.category}</Accordion.Body>
            </Accordion.Item>
            <Accordion.Item eventKey="1">
              <Accordion.Header>Descripción</Accordion.Header>
              <Accordion.Body>{garment.description}</Accordion.Body>
            </Accordion.Item>
            <Accordion.Item eventKey="2">
              <Accordion.Header>Características</Accordion.Header>
              <Accordion.Body>{garment.features}</Accordion.Body>
            </Accordion.Item>
          </Accordion>
        </Col>
      </Row>

      {isLogged && (
        <Container className="mt-5" ref={formRef}>
          <div className="container-header-form">
            <h3>{editingOpinion ? "Editar comentario" : "Añadir comentario"}</h3>
          </div>
          <hr className="separator-form" />
          
          <Form action={opinionAction}>
            {opinionState?.error && (
              <Alert variant="danger">{opinionState.error}</Alert>
            )}
            
            {editingOpinion && <input type="hidden" name="opinionId" value={editingOpinion.id} />}
            
            <Form.Group className="mb-3">
              <Form.Label><b>Valoración</b></Form.Label>
              <Form.Select 
                name="rating" 
                defaultValue={editingOpinion?.rating || ""}
                key={editingOpinion?.id || "new"}
                required
              >
                {!editingOpinion && <option value="" disabled>Selecciona una puntuación</option>}
                <option value="5">★★★★★ (5 estrellas)</option>
                <option value="4">★★★★☆ (4 estrellas)</option>
                <option value="3">★★★☆☆ (3 estrellas)</option>
                <option value="2">★★☆☆☆ (2 estrellas)</option>
                <option value="1">★☆☆☆☆ (1 estrella)</option>
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label><b>Comentario</b></Form.Label>
              <Form.Control
                as="textarea"
                rows={5}
                name="comment"
                placeholder="Comentario"
                minLength={5}
                maxLength={50}
                defaultValue={editingOpinion?.comment || ""}
                key={editingOpinion?.id || "new"}
                required
              />
              <Form.Text className="text-muted">
                El comentario debe tener entre 5 y 50 caracteres.
              </Form.Text>
            </Form.Group>

            <div className="mb-3">
              <Button 
                variant="success" 
                className="text-dark fw-bold me-2" 
                type="submit"
                disabled={isPendingOpinion}
              >
                {isPendingOpinion ? (
                  <><Spinner size="sm" className="me-2" />Enviando...</>
                ) : (
                  <>Enviar <i className="bi bi-chat-left-dots"></i></>
                )}
              </Button>
              {editingOpinion && (
                <Button 
                  variant="danger" 
                  className="text-dark fw-bold"
                  onClick={() => setEditingOpinion(null)}
                >
                  Cancelar <i className="bi bi-x-lg"></i>
                </Button>
              )}
            </div>
          </Form>
        </Container>
      )}

      <Container className="mt-4">
        <h3 className="mb-3">Opiniones</h3>
        {opinions.length === 0 ? (
          <Alert variant="info">No hay opiniones disponibles.</Alert>
        ) : (
          opinions.map((opinion) => (
            <Card key={opinion.id} className="p-3 mb-3 bg-light">
              <h5>Puntuación: {renderStars(opinion.rating)} ({opinion.rating} estrellas)</h5>
              <p className="mb-1">{opinion.comment}</p>
              
              {canEditOpinion(opinion) && (
                <div className="d-flex gap-2 mt-2">
                  <Button 
                    variant="primary" 
                    size="sm" 
                    className="text-dark"
                    onClick={() => handleEditOpinion(opinion)}
                  >
                    Editar <i className="bi bi-pencil-square"></i>
                  </Button>
                  <Button 
                    variant="danger" 
                    size="sm" 
                    className="text-dark"
                    onClick={() => handleDeleteOpinion(opinion.id)}
                  >
                    Borrar <i className="bi bi-x-lg"></i>
                  </Button>
                </div>
              )}
            </Card>
          ))
        )}

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
      </Container>
    </Container>
  );
}
