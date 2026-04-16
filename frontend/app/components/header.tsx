import "./header.css";
import { useNavigate, Link } from "react-router";
import Navbar from "react-bootstrap/Navbar";
import Nav from "react-bootstrap/Nav";
import NavDropdown from "react-bootstrap/NavDropdown";
import Container from "react-bootstrap/Container";
import { useUserStore } from "~/stores/user-store";

export default function Header() {
  const { user, logoutUser } = useUserStore();
  const isAdmin = user?.roles?.includes("ADMIN") ?? false;
  const navigate = useNavigate();

  async function handleLogout() {
    await logoutUser();
    navigate("/login");
  }

  return (
    <Navbar className="navbar-dark" expand="lg" sticky="top">
      <Container fluid>
        <Navbar.Brand as={Link as any} to="/">
          <img src="/logo.png" alt="Logo" className="img-logo" />
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="main-nav" />
        <Navbar.Collapse id="main-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link as any} to="/" className="nav-link-header">Home</Nav.Link>
            {isAdmin && (
              <>
                <Nav.Link as={Link as any} to="/users" className="nav-link-header">Gestionar usuarios</Nav.Link>
                <Nav.Link as={Link as any} to="/orders" className="nav-link-header">Gestionar pedidos</Nav.Link>
                <Nav.Link as={Link as any} to="/statistics" className="nav-link-header">Estadísticas</Nav.Link>
              </>
            )}
          </Nav>

          <Nav className="ms-auto">
            {user ? (
              <>
                <Nav.Link as={Link as any} to="/cart" className="nav-link-header">
                  <i className="bi bi-cart" /> Carrito
                </Nav.Link>
                <NavDropdown
                  title={
                    <span>
                      <i className="bi bi-person-circle" /> Perfil
                    </span>
                  }
                  align="end"
                >
                  <NavDropdown.Item as={Link as any} to={`/users/${user.id}`} className="nav-link-header">
                    <i className="bi bi-person me-2" />Gestionar perfil
                  </NavDropdown.Item>
                  <NavDropdown.Item as={Link as any} to="/myorders" className="nav-link-header">
                    <i className="bi bi-bag-check me-2" />Mis pedidos
                  </NavDropdown.Item>
                  <NavDropdown.Divider />
                  <NavDropdown.Item
                    className="text-danger"
                    onClick={handleLogout}
                  >
                    <i className="bi bi-box-arrow-right me-2" />Cerrar sesión
                  </NavDropdown.Item>
                </NavDropdown>
              </>
            ) : (
              <>
                <Nav.Link as={Link as any} to="/register" className="nav-link-header">
                  <i className="bi bi-person" /> Registrarse
                </Nav.Link>
                <Nav.Link as={Link as any} to="/login" className="nav-link-header">
                  <i className="bi bi-box-arrow-in-right" /> Iniciar sesión
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}