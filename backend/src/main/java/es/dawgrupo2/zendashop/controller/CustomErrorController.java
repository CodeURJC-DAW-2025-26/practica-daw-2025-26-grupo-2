package es.dawgrupo2.zendashop.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = (status != null) ? Integer.parseInt(status.toString()) : 500;

        Map<String, Object> attrs = errorAttributes.getErrorAttributes(
                new ServletWebRequest(request),
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));
        String springMessage = (String) attrs.getOrDefault("message", "Error inesperado");

        String originalUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (originalUri != null && originalUri.startsWith("/api/")) {
            String errorMessage;
            if (statusCode == 404) {
                errorMessage = "La ruta no existe, te has perdido en el ciberespacio";
            } else if (statusCode == 401) {
                if (originalUri.contains("/login")) {
                    errorMessage = "Credenciales incorrectas. Revisa tu usuario y contraseña";
                } else {
                    errorMessage = "No estás autenticado. Inicia sesión para acceder";
                }
            } else if (statusCode == 403) {
                errorMessage = "No tienes permiso para acceder a este recurso";
            } else {
                errorMessage = springMessage;
            }
            Map<String, Object> body = Map.of(
                    "error", errorMessage,
                    "status", statusCode);
            return ResponseEntity.status(statusCode).body(body);
        }

        if (statusCode == 404) {
            model.addAttribute("message", "Página no encontrada, te has perdido en el ciberespacio");
            model.addAttribute("backLink", "/");
        } else if (statusCode == 403) {
            model.addAttribute("message", "Acceso denegado, no puedes estar aquí");
            model.addAttribute("backLink", "/");
        } else {
            model.addAttribute("message", springMessage);
            model.addAttribute("backLink", "/");
        }
        return "customError";
    }
}
