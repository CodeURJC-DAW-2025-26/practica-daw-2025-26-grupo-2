package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;

import es.dawgrupo2.zendashop.repository.UserRepository;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.User;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Optional<User> findById(long id) {
		return repository.findById(id);
	}

	public List<User> findById(List<Long> ids) {
		return repository.findAllById(ids);
	}

	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<User> findAll() {
		return repository.findAll();
	}

	public void save(User user) {
		repository.save(user);
	}

	public void save(User user, MultipartFile imageFile) throws IOException {
		if (!imageFile.isEmpty()) {
			try {
				user.setAvatar(new SerialBlob(imageFile.getBytes()));
			} catch (Exception e) {
				throw new IOException("Failed to create image blob", e);
			}
		}
		this.save(user);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}

	public Optional<User> findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public void disableUser(long id) {
		User user = repository.findById(id).orElseThrow();
		if (user != null) {
			user.setDisabled(true);
			if (user.getOrders().size() > 0) {
				String uuid = UUID.randomUUID().toString();
				user.setName("Anonymous" + uuid);
				user.setEmail(uuid + "@anonymous.com");
				user.setEncodedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
				orderService.deleteCart(user.getCart());
				this.save(user);
			} else {
				if (user.getCart() != null) {
					orderService.deleteCart(user.getCart());
				}
				this.delete(id);
			}

		}
	}

	public Page<User> findByDisabledFalse(Pageable pageable) {
		return repository.findByDisabledFalse(pageable);
	}

	public String validateFields(User user) {

		String errorMsg = "";

		if (user.getName() == null || user.getName().isEmpty() || user.getName().length() < 4
				|| user.getName().length() > 50) {
			return errorMsg += "El nombre del usuario no puede estar vacío o tener menos de 4 caracteres o más de 50.";
		}

		if (user.getSurname() == null || user.getSurname().isEmpty() || user.getSurname().length() < 5
				|| user.getSurname().length() > 100) {
			return errorMsg += "Los apellidos del usuario no pueden estar vacíos o tener menos de 5 caracteres o más de 100.";
		}

		if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().length() < 3
				|| user.getEmail().length() > 90) {
			return errorMsg += "El email del usuario no puede estar vacío o tener menos de 3 caracteres o más de 90.";
		}

		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
		if (!user.getEmail().matches(emailRegex)) {
			return "El email del usuario no tiene un formato válido.";
		}

		if (user.getEncodedPassword() == null || user.getEncodedPassword().isEmpty()
				|| user.getEncodedPassword().length() < 8 || user.getEncodedPassword().length() > 100) {
			return errorMsg += "La contraseña del usuario no puede estar vacía o tener menos de 8 caracteres o más de 100.";
		}

		if (user.getAdress() != null && (user.getAdress().length() < 10 || user.getAdress().length() > 150)) {
			return errorMsg += "La dirección del usuario no puede tener menos de 10 caracteres o más de 150.";
		}
		return errorMsg;
	}
}
