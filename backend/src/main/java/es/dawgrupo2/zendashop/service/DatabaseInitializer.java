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
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {

		String encodedPass1 = passwordEncoder.encode("password123");
		String encodedPass2 = passwordEncoder.encode("password456");
		String encodedPass3 = passwordEncoder.encode("password789");
		String encodedPass4 = passwordEncoder.encode("password101");
		String encodedPass5 = passwordEncoder.encode("password102");
		String encodedPass6 = passwordEncoder.encode("password103");
		String encodedPass7 = passwordEncoder.encode("password104");
		String encodedPass8 = passwordEncoder.encode("password105");
		String encodedPass9 = passwordEncoder.encode("password106");
		String encodedPass10 = passwordEncoder.encode("password107");
		String encodedPass11 = passwordEncoder.encode("password108");
		String encodedPass12 = passwordEncoder.encode("password109");
		String encodedPass13 = passwordEncoder.encode("password110");
		String encodedPass14 = passwordEncoder.encode("password111");
		String encodedPass15 = passwordEncoder.encode("password112");
		String encodedPass16 = passwordEncoder.encode("password113");
		String encodedPass17 = passwordEncoder.encode("password114");

		User user1 = new User("Juan", "González", "juan@example.com", "Avenida Rey Juan", encodedPass1, "USER");
		userService.save(user1);

		User user2 = new User("Maria", "Martínez", "maria@example.com", "Calle Carlos I",	 encodedPass2, "ADMIN");
		userService.save(user2);

		User user3 = new User("Carlos", "López", "carlos@example.com", "Calle Mayor 1", encodedPass3, "USER");
		userService.save(user3);

		User user4 = new User("Ana", "Ruiz", "ana@example.com", "Calle Mayor 2", encodedPass4, "USER");
		userService.save(user4);

		User user5 = new User("Pedro", "Sánchez", "pedro@example.com", "Calle Mayor 3", encodedPass5, "USER");
		userService.save(user5);

		User user6 = new User("Lucía", "Fernández", "lucia@example.com", "Calle Mayor 4", encodedPass6, "USER");
		userService.save(user6);

		User user7 = new User("Javier", "Torres", "javier@example.com", "Calle Mayor 5", encodedPass7, "USER");
		userService.save(user7);

		User user8 = new User("Elena", "Romero", "elena@example.com", "Calle Mayor 6", encodedPass8, "USER");
		userService.save(user8);

		User user9 = new User("Miguel", "Navarro", "miguel@example.com", "Calle Mayor 7", encodedPass9, "USER");
		userService.save(user9);

		User user10 = new User("Laura", "Domínguez", "laura@example.com", "Calle Mayor 8", encodedPass10, "USER");
		userService.save(user10);

		User user11 = new User("Sergio", "Vázquez", "sergio@example.com", "Calle Mayor 9", encodedPass11, "USER");
		userService.save(user11);

		User user12 = new User("Paula", "Ortega", "paula@example.com", "Calle Mayor 10", encodedPass12, "ADMIN");
		userService.save(user12);

		User user13 = new User("Raúl", "Castro", "raul@example.com", "Calle Mayor 11", encodedPass13, "USER");
		userService.save(user13);

		User user14 = new User("Carmen", "Molina", "carmen@example.com", "Calle Mayor 12", encodedPass14, "USER");
		userService.save(user14);

		User user15 = new User("David", "Delgado", "david@example.com", "Calle Mayor 13", encodedPass15, "USER");
		userService.save(user15);

		User user16 = new User("Marta", "Gil", "marta@example.com", "Calle Mayor 14", encodedPass16, "USER");
		userService.save(user16);

		User user17 = new User("Álvaro", "Herrera", "alvaro@example.com", "Calle Mayor 15", encodedPass17, "ADMIN");
		userService.save(user17);

		Garment garment1 = new Garment("Camiseta blanca", BigDecimal.valueOf(19.99), "Camisas", "Camiseta de algodón",
				"Pequeño, color blanco");
		
		Garment garment2 = new Garment("Pantalones", BigDecimal.valueOf(39.99), "Pantalones", "Pantalones vaqueros",
				"Grande, color azul");

		Garment garment3 = new Garment("Camiseta azul", BigDecimal.valueOf(39.99), "Camisas", "Camiseta de algodón",
				"Mediano, color azul");
		
		Garment garment4 = new Garment("Camiseta roja", BigDecimal.valueOf(29.99), "Camisas", "Camiseta de algodón",
				"Pequeño, color rojo");

		Garment garment5 = new Garment("Jersey", BigDecimal.valueOf(59.99), "Chaquetas", "Jersey de lana",
				"Grande, color marrón");
		
		Garment garment6 = new Garment("Camiseta amarilla", BigDecimal.valueOf(49.99), "Camisas", "Camiseta de verano",
				"Mediano, color amarillo");
		
		Garment garment7 = new Garment("Camiseta gris", BigDecimal.valueOf(34.99), "Camisas", "Camiseta de algodón",
				"Grande, color gris");

		Garment garment8 = new Garment("Camiseta verde", BigDecimal.valueOf(24.99), "Camisas", "Camiseta de algodón",
				"Mediano, color verde");

		Garment garment9 = new Garment("Camiseta rosa", BigDecimal.valueOf(29.99), "Camisas", "Camiseta de seda",
				"Pequeño, color rosa");

		Garment garment10 = new Garment("Camiseta azul marino", BigDecimal.valueOf(79.99), "Camisas", "Camiseta de lana",
				"Mediano, color azul marino");

		Garment garment11 = new Garment("Camiseta negra", BigDecimal.valueOf(29.99), "Camisas", "Camiseta de algodón",
				"Grande, color negro");

		Garment garment12 = new Garment("Camiseta turquesa", BigDecimal.valueOf(34.99), "Camisas", "Camiseta de manga larga de algodón",
				"Mediano, color azul claro");

		Garment garment13 = new Garment("Zapatos", BigDecimal.valueOf(89.99), "Calzado", "Zapatos de cuero", 
				"Bonitos, elegantes");

		Garment garment14 = new Garment("Sudadera", BigDecimal.valueOf(49.99), "Chaquetas", "Sudadera con capucha de algodón", 
				"Cómoda, cálida");

		Garment garment15 = new Garment("Bolso negro", BigDecimal.valueOf(99.99), "Accesorios", "Bolso de cuero", 
				"Resistente, elegante");

		garmentService.save(garment1, convertImage("sample_images/camiseta.jpg"));
		garmentService.save(garment2, convertImage("sample_images/pantalon.jpg"));
		garmentService.save(garment3, convertImage("sample_images/camiseta-azul.jpg"));
		garmentService.save(garment4, convertImage("sample_images/camiseta-roja.jpg"));
		garmentService.save(garment5, convertImage("sample_images/jersey.jpg"));
		garmentService.save(garment6, convertImage("sample_images/camiseta-amarilla.jpg"));
		garmentService.save(garment7, convertImage("sample_images/camiseta-gris.jpg"));
		garmentService.save(garment8, convertImage("sample_images/camiseta-verde.jpg"));
		garmentService.save(garment9, convertImage("sample_images/camiseta-rosa.jpg"));
		garmentService.save(garment10, convertImage("sample_images/camiseta-azul-marino.jpg"));
		garmentService.save(garment11, convertImage("sample_images/camiseta-negra.jpg"));
		garmentService.save(garment12, convertImage("sample_images/camiseta-turquesa.jpg"));
		garmentService.save(garment13, convertImage("sample_images/zapatos.jpg"));
		garmentService.save(garment14, convertImage("sample_images/sudadera.jpg"));
		garmentService.save(garment15, convertImage("sample_images/bolso-negro.jpg"));

		Opinion opinion1 = new Opinion(4, "Buena calidad, pero el color no es exactamente como en la foto.");
		Opinion opinion2 = new Opinion(5, "¡Me ha encantado! La camiseta es muy cómoda.");
		Opinion opinion3 = new Opinion(3, "La talla no es la correcta, pero el material es bueno.");
		Opinion opinion4 = new Opinion(4, "El diseño es bonito, pero la tela podría ser más suave.");
		Opinion opinion5 = new Opinion(1, "Malísima");
		Opinion opinion6 = new Opinion(5, "Excelente, superó mis expectativas.");
		Opinion opinion7 = new Opinion(4, "Buena relación calidad-precio.");
		Opinion opinion8 = new Opinion(2, "No me gustó, esperaba más.");
		Opinion opinion9 = new Opinion(5, "Perfecto, tal como lo esperaba.");
		Opinion opinion10 = new Opinion(3, "Está bien, pero podría ser mejor.");
		Opinion opinion11 = new Opinion(4, "Muy buena calidad, pero el diseño no es mi favorito.");
		Opinion opinion12 = new Opinion(5, "Increíble, superó todas mis expectativas.");
		
		garment1.addOpinion(opinion1);
		garment2.addOpinion(opinion2);
		garment1.addOpinion(opinion3);
		garment1.addOpinion(opinion4);
		garment1.addOpinion(opinion5);
		garment1.addOpinion(opinion6);
		garment1.addOpinion(opinion7);
		garment1.addOpinion(opinion8);
		garment1.addOpinion(opinion9);
		garment1.addOpinion(opinion10);
		garment1.addOpinion(opinion11);
		garment1.addOpinion(opinion12);
		user1.addOpinion(opinion1);
		user2.addOpinion(opinion2);
		user1.addOpinion(opinion3);
		user1.addOpinion(opinion4);
		user2.addOpinion(opinion5);
		user1.addOpinion(opinion6);
		user1.addOpinion(opinion7);
		user1.addOpinion(opinion8);
		user2.addOpinion(opinion9);
		user1.addOpinion(opinion10);
		user1.addOpinion(opinion11);
		user1.addOpinion(opinion12);
		garmentService.save(garment1);
		garmentService.save(garment2);

		Order order1 = new Order(false, "C/ Falsa 123", LocalDate.now(), "Dejar en portería");
		user1.setCart(order1); 
		order1.setUser(user1);
		order1.addOrderItem(new OrderItem(2, "M", garment1));
		order1.addOrderItem(new OrderItem(1, "L", garment2));
		order1.addOrderItem(new OrderItem(1, "S", garment3));
		order1.addOrderItem(new OrderItem(1, "M", garment4));
		order1.addOrderItem(new OrderItem(1, "L", garment5));
		order1.addOrderItem(new OrderItem(1, "M", garment6));
		order1.addOrderItem(new OrderItem(1, "L", garment7));
		order1.addOrderItem(new OrderItem(1, "M", garment8));
		order1.addOrderItem(new OrderItem(1, "S", garment9));
		order1.addOrderItem(new OrderItem(1, "M", garment10));
		order1.addOrderItem(new OrderItem(1, "L", garment11));
		order1.addOrderItem(new OrderItem(1, "M", garment12));

		orderService.save(order1);
		userService.save(user1);

		Order order2 = new Order(true, "C/ Admin 1", LocalDate.now(), "Entrega urgente");
		user2.addOrder(order2);
		order2.addOrderItem(new OrderItem(1, "L", garment2));

		orderService.save(order2);

		Order order3 = new Order(true, "C/ Admin 1", LocalDate.now(), "Entrega urgente");
		user2.addOrder(order3);
		order3.addOrderItem(new OrderItem(10, "L", garment2));
		orderService.save(order3);

		Order order4 = new Order(true, "C/ Admin 1", LocalDate.now(), "Entrega urgente");
		user2.addOrder(order4);
		order4.addOrderItem(new OrderItem(10, "L", garment2));
		orderService.save(order4);

		Order order5 = new Order(true, "C/ Mayor 2", LocalDate.now(), "Entrega estándar");
		user3.addOrder(order5);
		order5.addOrderItem(new OrderItem(1, "M", garment4));
		orderService.save(order5);

		Order order6 = new Order(true, "Av. Andalucía 15", LocalDate.now(), "Pago pendiente");
		user4.addOrder(order6);
		order6.addOrderItem(new OrderItem(2, "L", garment5));
		orderService.save(order6);

		Order order7 = new Order(true, "C/ Sol 8", LocalDate.now(), "Entrega en horario laboral");
		user5.addOrder(order7);
		order7.addOrderItem(new OrderItem(1, "M", garment6));
		orderService.save(order7);

		Order order8 = new Order(true, "C/ Luna 3", LocalDate.now(), "Sin observaciones");
		user6.addOrder(order8);
		order8.addOrderItem(new OrderItem(1, "L", garment7));
		orderService.save(order8);

		Order order9 = new Order(true, "C/ Norte 10", LocalDate.now(), "Pendiente confirmación");
		user7.addOrder(order9);
		order9.addOrderItem(new OrderItem(3, "M", garment8));
		orderService.save(order9);

		Order order10 = new Order(true, "C/ Sur 22", LocalDate.now(), "Regalo");
		user8.addOrder(order10);
		order10.addOrderItem(new OrderItem(1, "S", garment9));
		orderService.save(order10);

		Order order11 = new Order(true, "C/ Este 5", LocalDate.now(), "Entrega rápida");
		user9.addOrder(order11);
		order11.addOrderItem(new OrderItem(2, "M", garment10));
		orderService.save(order11);

		Order order12 = new Order(true, "C/ Oeste 14", LocalDate.now(), "Pago contra reembolso");
		user10.addOrder(order12);
		order12.addOrderItem(new OrderItem(1, "L", garment11));
		orderService.save(order12);

		Order order13 = new Order(true, "Av. Libertad 7", LocalDate.now(), "Cliente frecuente");
		user11.addOrder(order13);
		order13.addOrderItem(new OrderItem(1, "M", garment12));
		orderService.save(order13);

		Order order14 = new Order(true, "C/ Jardín 4", LocalDate.now(), "Entrega antes del viernes");
		user12.addOrder(order14);
		order14.addOrderItem(new OrderItem(1, "40", garment3));
		orderService.save(order14);

		Order order15 = new Order(true, "C/ Río 9", LocalDate.now(), "Pendiente stock");
		user13.addOrder(order15);
		order15.addOrderItem(new OrderItem(2, "S", garment1));
		orderService.save(order15);

		Order order16 = new Order(true, "C/ Montaña 6", LocalDate.now(), "Urgente");
		user14.addOrder(order16);
		order16.addOrderItem(new OrderItem(1, "M", garment5));
		orderService.save(order16);

		Order order17 = new Order(true, "C/ Playa 12", LocalDate.now(), "Entrega en oficina");
		user15.addOrder(order17);
		order17.addOrderItem(new OrderItem(1, "L", garment6));
		orderService.save(order17);

		Order order18 = new Order(true, "C/ Bosque 18", LocalDate.now(), "Pago pendiente");
		user16.addOrder(order18);
		order18.addOrderItem(new OrderItem(3, "M", garment7));
		orderService.save(order18);

		Order order19 = new Order(true, "C/ Centro 1", LocalDate.now(), "Entrega 24h");
		user17.addOrder(order19);
		order19.addOrderItem(new OrderItem(1, "42", garment8));
		orderService.save(order19);

		Order order20 = new Order(true, "C/ Futura 99", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order20);
		order20.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order20);

		Order order21 = new Order(true, "C/ Futura 100", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order21);
		order21.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order21);

		Order order22 = new Order(true, "C/ Futura 101", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order22);
		order22.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order22);

		Order order23 = new Order(true, "C/ Futura 102", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order23);
		order23.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order23);

		Order order24 = new Order(true, "C/ Futura 103", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order24);
		order24.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order24);

		Order order25 = new Order(true, "C/ Futura 104", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order25);
		order25.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order25);
		
		Order order26 = new Order(true, "C/ Futura 105", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order26);
		order26.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order26);

		Order order27 = new Order(true, "C/ Futura 106", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order27);
		order27.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order27);

		Order order28 = new Order(true, "C/ Futura 107", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order28);
		order28.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order28);

		Order order29 = new Order(true, "C/ Futura 108", LocalDate.now(), "Sin observaciones");
		user2.addOrder(order29);
		order29.addOrderItem(new OrderItem(1, "M", garment1));
		orderService.save(order29);

		// Update creation dates to have orders in different periods for statistics testing
		orderService.forceCreationDate(order1.getId(), LocalDate.now().minusDays(1).atStartOfDay());
		orderService.forceCreationDate(order2.getId(), LocalDate.now().minusMonths(1).atStartOfDay());
		orderService.forceCreationDate(order3.getId(), LocalDate.now().minusYears(1).atStartOfDay());
		orderService.forceCreationDate(order4.getId(), LocalDate.now().minusYears(3).atStartOfDay());
		orderService.forceCreationDate(order5.getId(),  LocalDate.now().minusDays(2).atStartOfDay());
		orderService.forceCreationDate(order6.getId(),  LocalDate.now().minusDays(5).atStartOfDay());
		orderService.forceCreationDate(order7.getId(),  LocalDate.now().minusWeeks(2).atStartOfDay());

		orderService.forceCreationDate(order8.getId(),  LocalDate.now().minusMonths(2).atStartOfDay());
		orderService.forceCreationDate(order9.getId(),  LocalDate.now().minusMonths(4).atStartOfDay());
		orderService.forceCreationDate(order10.getId(), LocalDate.now().minusMonths(6).atStartOfDay());

		orderService.forceCreationDate(order11.getId(), LocalDate.now().minusYears(1).atStartOfDay());
		orderService.forceCreationDate(order12.getId(), LocalDate.now().minusYears(1).minusMonths(3).atStartOfDay());

		orderService.forceCreationDate(order13.getId(), LocalDate.now().minusYears(2).atStartOfDay());
		orderService.forceCreationDate(order14.getId(), LocalDate.now().minusYears(2).minusMonths(6).atStartOfDay());

		orderService.forceCreationDate(order15.getId(), LocalDate.now().minusYears(3).atStartOfDay());
		orderService.forceCreationDate(order16.getId(), LocalDate.now().minusYears(3).minusMonths(4).atStartOfDay());

		orderService.forceCreationDate(order17.getId(), LocalDate.now().minusYears(4).atStartOfDay());
		orderService.forceCreationDate(order18.getId(), LocalDate.now().minusYears(5).atStartOfDay());

		orderService.forceCreationDate(order19.getId(), LocalDate.now().minusYears(6).atStartOfDay());
	}

	private MockMultipartFile convertImage(String imageUrl) throws IOException {
		Resource image = new ClassPathResource(imageUrl);
		InputStream inputStream = image.getInputStream();

		MockMultipartFile multipartFile = new MockMultipartFile(
				"image",
				"camiseta.jpg",
				"image/jpg",
				StreamUtils.copyToByteArray(inputStream));
		return multipartFile;
	}
}