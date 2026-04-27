import "./footer.css";

export default function Footer() {
  return (
    <footer className="bg-dark text-white py-5">
      <div className="container">
        <div className="row gy-4">
          <div className="col-12 col-md-4">
            <h6 className="text-uppercase mb-3">Atención al cliente</h6>
            <ul className="list-unstyled mb-0">
              <li className="mb-2"><a href="#" className="footer-link text-decoration-none text-white-50">FAQ</a></li>
              <li className="mb-2"><a href="#" className="footer-link text-decoration-none text-white-50">Política de privacidad</a></li>
              <li className="mb-2"><a href="#" className="footer-link text-decoration-none text-white-50">Términos de servicio</a></li>
              <li><a href="#" className="footer-link text-decoration-none text-white-50">Contacto</a></li>
            </ul>
          </div>

          <div className="col-12 col-md-4">
            <h6 className="text-uppercase mb-3">Empresa</h6>
            <ul className="list-unstyled mb-0">
              <li className="mb-2"><a href="#" className="footer-link text-decoration-none text-white-50">Sobre ZENDA</a></li>
              <li className="mb-2"><a href="#" className="footer-link text-decoration-none text-white-50">Envíos y devoluciones</a></li>
              <li className="mb-2"><a href="#" className="footer-link text-decoration-none text-white-50">Política de cookies</a></li>
              <li><a href="#" className="footer-link text-decoration-none text-white-50">Aviso legal</a></li>
            </ul>
          </div>

          <div className="col-12 col-md-4">
            <h6 className="text-uppercase mb-3">Síguenos</h6>
            <div className="d-flex gap-3">
              <a className="social-link text-white" href="https://www.instagram.com" target="_blank" rel="noreferrer">
                <i className="bi bi-instagram" />
              </a>
              <a className="social-link text-white" href="https://www.facebook.com" target="_blank" rel="noreferrer">
                <i className="bi bi-facebook" />
              </a>
              <a className="social-link text-white" href="https://github.com" target="_blank" rel="noreferrer">
                <i className="bi bi-github" />
              </a>
            </div>
            <div className="mt-3 text-white-50">
              Soporte: soporte@zenda.com<br />
              Horario: L–V 9:00–18:00
            </div>
          </div>
        </div>

        <hr className="border-secondary my-4" />

        <div className="d-flex flex-column flex-md-row justify-content-between align-items-center gap-2">
          <div className="text-white-50 small">© 2026 ZENDA Shop. Todos los derechos reservados</div>
          <div className="text-white-50 small">Hecho con Bootstrap · Diseño consistente</div>
        </div>
      </div>
    </footer>
  );
}