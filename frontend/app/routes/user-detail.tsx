import "./user-detail.css";
import { redirect, useNavigate, Link } from "react-router";
import type { Route } from "./+types/user-detail";
import { Container, Button, Row, Col, Badge } from "react-bootstrap";
import { getUser, disableUser } from "~/services/users-service";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader({ request, params }: Route.ClientLoaderArgs) {
  const { user } = useUserStore.getState();

  if (!user) {
    const url = new URL(request.url);
    throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
  }

  const profileId = Number(params.id);

  if (!user.roles?.includes("ADMIN") && user.id !== profileId) {
    throw redirect("/");
  }

  const profileUser = await getUser(profileId);
  return { profileUser };
}

export default function UserDetail({ loaderData }: Route.ComponentProps) {
  const { profileUser } = loaderData;
  const loggedUser = useUserStore((s) => s.user);
  const navigate = useNavigate();

  const isAdmin = loggedUser?.roles?.includes("ADMIN");
  const isOwn = loggedUser?.id === profileUser.id;
  const canEdit = isAdmin || isOwn;

  const avatarSrc = profileUser.avatar
    ? `/api/v1/images/${profileUser.avatar.id}/media`
    : "/avatar_default.png";

  const formattedDate = profileUser.creationDate
    ? new Date(profileUser.creationDate).toLocaleDateString("es-ES")
    : "—";

  async function handleDelete() {
    if (!confirm("¿Seguro que quieres eliminar esta cuenta?")) return;
    await disableUser(profileUser.id);
    navigate(isAdmin ? "/users" : "/");
  }

  return (
    <Container className="container-main mt-5 mb-5">
      <section className="profile-shell">

        <div className="profile-header">
          <div className="d-flex align-items-center gap-3">
            <div className="profile-avatar">
              <img
                src={avatarSrc}
                alt="Avatar"
                className="rounded-circle mb-2"
                width={96}
                height={96}
                style={{ objectFit: "cover" }}
              />
            </div>
            <div className="profile-identity">
              <h1 className="profile-name mb-1">
                {profileUser.name} {profileUser.surname}
              </h1>
              <div className="profile-meta">
                <span className="me-3">
                  <i className="bi bi-envelope" /> {profileUser.email}
                </span>
              </div>
            </div>
          </div>

          {canEdit && (
            <div className="profile-actions d-flex align-items-center gap-2 mt-2">
              <Link
                to={`/users/${profileUser.id}/edit`}
                className="btn btn-warning text-dark"
              >
                Editar perfil <i className="bi bi-pencil-square" />
              </Link>
              <Button variant="danger" onClick={handleDelete}>
                Eliminar cuenta <i className="bi bi-x-lg" />
              </Button>
            </div>
          )}
        </div>

        <hr className="profile-divider" />

        <Row className="g-4">
          <Col xs={12} lg={5}>
            <div className="profile-card">
              <div className="profile-card-title">
                <h3 className="mb-0">Datos de la cuenta</h3>
              </div>

              <div className="profile-kv">
                <div className="profile-kv-row">
                  <div className="profile-k">Nombre</div>
                  <div className="profile-v">{profileUser.name}</div>
                </div>
                <div className="profile-kv-row">
                  <div className="profile-k">Apellidos</div>
                  <div className="profile-v">{profileUser.surname || "—"}</div>
                </div>
                <div className="profile-kv-row">
                  <div className="profile-k">Email</div>
                  <div className="profile-v">{profileUser.email}</div>
                </div>
                <div className="profile-kv-row">
                  <div className="profile-k">Dirección</div>
                  <div className="profile-v">{profileUser.address || "—"}</div>
                </div>
                <div className="profile-kv-row">
                  <div className="profile-k">Fecha de registro</div>
                  <div className="profile-v">{formattedDate}</div>
                </div>
                <div className="profile-kv-row">
                  <div className="profile-k">Rol</div>
                  <div className="profile-v">
                    {profileUser.roles?.includes("ADMIN") ? (
                      <Badge bg="danger">Admin</Badge>
                    ) : (
                      <Badge bg="secondary">Usuario</Badge>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </Col>

          <Col xs={12} lg={7}>
            <div className="profile-card h-100">
              <div className="profile-card-title">
                <h3 className="mb-0">Pedidos</h3>
              </div>
              <span className="profile-subtitle">
                Historial de compras de este usuario
              </span>

              <div className="profile-card-footer">
                <Link
                  to={`/orders?userId=${profileUser.id}`}
                  className="btn btn-primary"
                >
                  Ver pedidos <i className="bi bi-bag-check" />
                </Link>
              </div>
            </div>
          </Col>
        </Row>

      </section>
    </Container>
  );
}
