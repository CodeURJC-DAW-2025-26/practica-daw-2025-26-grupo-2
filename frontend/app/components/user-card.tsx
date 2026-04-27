import { Link } from "react-router";
import { Button } from "react-bootstrap";
import type UserBasicDTO from "~/dtos/UserBasicDTO";

interface Props {
  user: UserBasicDTO;
  onDelete?: (id: number) => void;
}

export default function UserCard({ user, onDelete }: Props) {
  const isAdmin = user.roles?.includes("ADMIN");
  const avatarSrc = user.avatar
    ? `/api/v1/images/${user.avatar.id}/media`
    : `${import.meta.env.BASE_URL}avatar_default.png`;

  return (
    <tr>
      <td>
        <img
          src={avatarSrc}
          alt={`Avatar de ${user.name}`}
          className="rounded-circle"
          width={40}
          height={40}
          style={{ objectFit: "cover" }}
        />
      </td>
      <td>{user.id}</td>
      <td>{user.name}</td>
      <td>{user.email}</td>
      <td>{isAdmin ? "Admin" : "Usuario"}</td>
      <td>
        <div className="d-flex flex-wrap gap-2">
          <Link to={`/users/${user.id}`} className="btn btn-info btn-sm">
            Ver <i className="bi bi-eye" />
          </Link>
          <Link to={`/users/${user.id}/edit`} className="btn btn-warning btn-sm">
            Editar <i className="bi bi-pencil-square" />
          </Link>
          <Button
            variant="danger"
            size="sm"
            onClick={() => onDelete?.(user.id)}
          >
            Borrar <i className="bi bi-x-lg" />
          </Button>
        </div>
      </td>
    </tr>
  );
}
