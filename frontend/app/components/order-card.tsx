import { Link } from "react-router";
import type OrderBasicDTO from "~/dtos/OrderBasicDTO";
import { Button } from "react-bootstrap";

interface Props {
  order: OrderBasicDTO;
  isAdmin: boolean;
  onDelete?: (id: number) => void;
}

export default function OrderCard({ order, isAdmin, onDelete }: Props) {
  const fecha = new Date(order.creationDate).toLocaleDateString("es-ES", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });

  return (
    <tr>
      <td>{order.id}</td>
      {isAdmin && <td>{order.user?.email ?? "-"}</td>}
      <td>{fecha}</td>
      <td>{order.totalPrice.toFixed(2)}€</td>
      <td>
        <div className="d-flex flex-wrap gap-2">
          <Link
            to={`/orders/${order.id}`}
            className="btn btn-info btn-sm"
          >
            Ver detalle <i className="bi bi-eye" />
          </Link>
          {isAdmin ? (
            <>
              {!order.completed && (
                <Button as={Link as any}
                  to={`/orders/${order.id}/edit`}
                  className="btn btn-warning btn-sm"
                >
                  Editar <i className="bi bi-pencil" />
                </Button>
              )}
              <Button
                className="btn btn-danger btn-sm"
                onClick={() => onDelete?.(order.id)}
              >
                Borrar <i className="bi bi-x-lg" />
              </Button>
            </>
          ) : (
            order.completed && (
              <a
                href={`/api/v1/orders/${order.id}/invoice`}
                className="btn btn-success btn-sm"
                target="_blank"
                rel="noreferrer"
              >
                Generar factura <i className="bi bi-file-earmark-pdf" />
              </a>
            )
          )}
        </div>
      </td>
    </tr>
  );
}