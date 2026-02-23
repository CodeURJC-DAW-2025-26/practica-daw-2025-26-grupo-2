package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;
//import org.springframework.security.crypto.password.PasswordEncoder;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.OrderItem;
import es.dawgrupo2.zendashop.model.User;

@Service
public class DatabaseInitializer {

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OpinionService opinionService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {

		String encodedPass1 = passwordEncoder.encode("password123");
		String encodedPass2 = passwordEncoder.encode("password456");

		User user1 = new User("Juan", "González", "juan@example.com", "Avenida Rey Juan", encodedPass1, "USER");
		userService.save(user1);

		User user2 = new User("Maria", "Martínez", "maria@example.com", "Calle Carlos I",	 encodedPass2, "ADMIN");
		userService.save(user2);

		Garment garment1 = new Garment("Camiseta", BigDecimal.valueOf(19.99), "Ropa", "Camiseta de algodón",
				"Talla M, color blanco");
		
		Garment garment2 = new Garment("Pantalones", BigDecimal.valueOf(39.99), "Ropa", "Pantalones vaqueros",
				"Talla 32, color azul");

		Resource image = new ClassPathResource("sample_images/camiseta.jpg");
		InputStream inputStream = image.getInputStream();

		MockMultipartFile multipartFile = new MockMultipartFile(
				"image",
				"camiseta.jpg",
				"image/jpg",
				StreamUtils.copyToByteArray(inputStream));

		garmentService.save(garment1, multipartFile);

		image = new ClassPathResource("sample_images/pantalon.jpg");
		inputStream = image.getInputStream();

		multipartFile = new MockMultipartFile(
				"image",
				"pantalon.jpg",
				"image/jpg",
				StreamUtils.copyToByteArray(inputStream));

		garmentService.save(garment2, multipartFile);

		Opinion opinion1 = new Opinion(4, "Buena calidad, pero el color no es exactamente como en la foto.");
		Opinion opinion2 = new Opinion(5, "¡Me ha encantado! La camiseta es muy cómoda.");
		
		garment1.addOpinion(opinion1);
		garment2.addOpinion(opinion2);
		user1.addOpinion(opinion1);
		user2.addOpinion(opinion2);
		garmentService.save(garment1);
		garmentService.save(garment2);

		Order order1 = new Order(true, "C/ Falsa 123", LocalDate.now(), "Dejar en portería");
		user1.addOrder(order1); 
		order1.addOrderItem(new OrderItem(2, "M", garment1));
		order1.addOrderItem(new OrderItem(1, "L", garment2));

		orderService.save(order1);

		Order order2 = new Order(true, "C/ Admin 1", LocalDate.now(), "Entrega urgente");
		user2.addOrder(order2);
		order2.addOrderItem(new OrderItem(1, "L", garment2));

		orderService.save(order2);
	}
}