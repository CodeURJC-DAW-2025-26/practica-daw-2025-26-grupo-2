import "./user-detail.css";
import { redirect, useNavigate, Link } from "react-router";
import type { Route } from "./+types/user-detail";
import { Container, Button, Row, Col } from "react-bootstrap";
import { getUser, disableUser, getUserOrderStats, getUserMeanTicketChart } from "~/services/users-service";
import { useUserStore } from "~/stores/user-store";
import { useEffect, useRef, useState } from "react";
import Chart from "chart.js/auto";

type OrderStats = {
  averageTicketLastMonth: number;
  orderCount: number;
};

export async function clientLoader({ request, params }: Route.ClientLoaderArgs) {
  await useUserStore.getState().loadLoggedUser();
  const { user } = useUserStore.getState();

  if (!user) {
    const url = new URL(request.url);
    throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
  }

  const profileId = Number(params.id);

  if (!user.roles?.includes("ADMIN") && user.id !== profileId) {
    throw redirect(`/error?message=${encodeURIComponent("Acceso no autorizado")}`);
  }

  const [profileUser, orderStats] = await Promise.all([
    getUser(profileId),
    getUserOrderStats(profileId),
  ]);

  return {
    profileUser: {
      ...profileUser,
      orderStats,
    },
  };
}

// ─── Gráfica de ticket medio ────────────────────────────────────────────────

function MeanTicketChart({ userId }: { userId: number }) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const chartRef = useRef<Chart | null>(null);
  const [period, setPeriod] = useState<"month" | "year">("month");
  const [chartData, setChartData] = useState<{ data: number[]; labels: string[] } | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    setLoading(true);
    setError(false);
    getUserMeanTicketChart(userId, period)
      .then(setChartData)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [userId, period]);

  useEffect(() => {
    if (!canvasRef.current || !chartData) return;
    if (chartRef.current) chartRef.current.destroy();
    chartRef.current = new Chart(canvasRef.current, {
      type: "line",
      data: {
        labels: chartData.labels,
        datasets: [
          {
            label: "Ticket medio (€)",
            data: chartData.data,
            tension: 0.3,
            fill: false,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: { legend: { position: "top" } },
        scales: { y: { beginAtZero: true } },
      },
    });

    return () => {
      chartRef.current?.destroy();
      chartRef.current = null;
    };
  }, [chartData]);

  return (
    <div className="chart-container mt-3">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="mb-0" style={{ fontSize: "1.05rem", fontWeight: 800 }}>
          Evolución del ticket medio
        </h3>
        <div className="btn-group btn-group-sm" role="group">
          <button
            className={`btn btn-outline-primary px-3 ${period === "month" ? "active" : ""}`}
            onClick={() => setPeriod("month")}
          >
            Mensual
          </button>
          <button
            className={`btn btn-outline-primary px-3 ${period === "year" ? "active" : ""}`}
            onClick={() => setPeriod("year")}
          >
            Anual
          </button>
        </div>
      </div>
      <div className="card border-0 shadow-sm">
        <div className="card-body">
          {loading && (
            <p className="text-muted text-center py-3" style={{ fontSize: "0.9rem" }}>
              Cargando gráfica…
            </p>
          )}
          {error && !loading && (
            <p className="text-danger text-center py-3" style={{ fontSize: "0.9rem" }}>
              No se pudo cargar la gráfica.
            </p>
          )}
          <canvas ref={canvasRef} height={110} style={{ display: loading || error ? "none" : "block" }} />
        </div>
      </div>
    </div>
  );
}

// ─── Componente principal ────────────────────────────────────────────────────

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

  const stats: OrderStats | undefined = profileUser.orderStats;

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
          {/* LEFT */}
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
              </div>
            </div>
          </Col>

          {/* RIGHT */}
          <Col xs={12} lg={7}>
            <div className="profile-card h-100">
              <div className="profile-card-title">
                <h3 className="mb-0">Actividad y métricas</h3>
                <span style={{ color: "var(--text-muted)", fontSize: "0.9rem", fontWeight: 500 }}>
                  Resumen de comportamiento de compra
                </span>
              </div>

              <div className="row g-3 mt-1">
                <div className="col-12 col-md-6">
                  <div className="metric">
                    <div className="metric-top">
                      <span>Ticket medio (último mes)</span>
                      <i className="bi bi-graph-up" />
                    </div>
                    <div className="metric-value">
                      {stats?.averageTicketLastMonth?.toFixed(2) ?? "0.00"}€
                    </div>
                    <div className="metric-hint">Precio promedio por pedido</div>
                  </div>
                </div>

                <div className="col-12 col-md-6">
                  <div className="metric">
                    <div className="metric-top">
                      <span>Compras (último mes)</span>
                      <i className="bi bi-bag-check" />
                    </div>
                    <div className="metric-value">{stats?.orderCount ?? 0}</div>
                    <div className="metric-hint">Pedidos procesados</div>
                  </div>
                </div>
              </div>

              <MeanTicketChart userId={profileUser.id} />

              <div className="profile-card-footer mt-3">
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