import { useState } from "react";
import { Link, redirect } from "react-router";
import type { Route } from "./+types/user-list";
import { Container, Table, Button, Spinner, Alert } from "react-bootstrap";
import { getUsers, disableUser } from "~/services/users-service";
import UserCard from "~/components/user-card";
import { useUserStore } from "~/stores/user-store";
import type UserBasicDTO from "~/dtos/UserBasicDTO";

const PAGE_SIZE = 10;


export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  await useUserStore.getState().loadLoggedUser();
  const { user } = useUserStore.getState();


  if (!user) {
    const url = new URL(request.url);
    throw redirect(`/login?from=${encodeURIComponent(url.pathname)}`);
  }

  if (!user.roles?.includes("ADMIN")) {
    throw redirect("/");
  }

  const users = await getUsers(0, PAGE_SIZE);
  return { users, hasMore: users.length === PAGE_SIZE };
}

export default function UserList({ loaderData }: Route.ComponentProps) {

  const [users, setUsers] = useState<UserBasicDTO[]>(loaderData.users);
  const [hasMore, setHasMore] = useState(loaderData.hasMore);
  const [page, setPage] = useState(1);      
  const [loading, setLoading] = useState(false);

  
  async function handleLoadMore() {
    setLoading(true);
    try {
      const more = await getUsers(page, PAGE_SIZE);
      setUsers((prev) => [...prev, ...more]); 
      setHasMore(more.length === PAGE_SIZE);  
      setPage(page + 1);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id: number) {
    await disableUser(id);
    setUsers(users.filter((u) => u.id !== id)); 
  }

  return (
    <Container className="container-main mt-5 mb-5">

      {/* Cabecera con título y botón de añadir */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="mb-0">Gestión de Usuarios</h2>
        <Link to="/users/new" className="btn btn-success">
          Añadir usuario <i className="bi bi-plus-circle" />
        </Link>
      </div>

      {/* Tabla de usuarios */}
      <div className="table-responsive">
        <Table bordered striped className="align-middle">
          <thead className="table-light">
            <tr>
              <th>Avatar</th>
              <th>ID</th>
              <th>Nombre</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 ? (
              // Lista vacía
              <tr>
                <td colSpan={6}>
                  <Alert variant="info" className="mb-0">No hay usuarios disponibles</Alert>
                </td>
              </tr>
            ) : (
              users.map((user: UserBasicDTO) => (
                <UserCard
                  key={user.id}           
                  user={user}             
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
