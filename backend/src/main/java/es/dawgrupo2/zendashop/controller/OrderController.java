package es.dawgrupo2.zendashop.controller;

import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.GarmentService;
import es.dawgrupo2.zendashop.service.OrderService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private GarmentService garmentService;

    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}

    @GetMapping("/orders")
    public String showOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders";
    }

	@GetMapping("/myorders")
	public String showUserOrders(Model model, Principal principal) {
		Optional<User> opUser = userService.findByEmail(principal.getName());
		model.addAttribute("orders", orderService.findByUserId(opUser.get().getId()));
        return "user_orders";
	}

	@GetMapping("addtocart/{garmentId}")
	public String addToCart(Model model, @PathVariable long garmentId, Principal principal) {
		// Method to find the order with status "in progress"
        Optional<Garment> opGarment = garmentService.findById(garmentId);
		if (!opGarment.isPresent()) {
			model.addAttribute("element", "Prenda");
			model.addAttribute("masculine", false);
			return "garment_not_found";
		}
		User user = userService.findByEmail(principal.getName()).orElseThrow();
        Order cart = user.getCart();
        if (cart != null) {
            cart.addGarment(opGarment.get());
            user.setCart(cart);
            orderService.save(cart);
        } else {
            Order newOrder = new Order(false, null, null, null, null);
            newOrder.addGarment(opGarment.get());
            user.setCart(newOrder);
            userService.save(user);
        }
        return "redirect:/";
	}

}