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
			if (user.getCart() != null) {
				orderService.deleteCart(user.getCart());
				this.save(user);
			}
			if (user.getOrders().size() > 0) {
				String uuid = UUID.randomUUID().toString();
				user.setName("Anonymous" + uuid);
				user.setEmail(uuid + "@anonymous.com");
				user.setEncodedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
				orderService.deleteCart(user.getCart());
				this.save(user);
			}
			else{
				this.delete(id);
			}
		}

	}

	public Page<User> findByDisabledFalse(Pageable pageable) {
		return repository.findByDisabledFalse(pageable);
	}

	public String validateFields(User user, boolean updatePassword) {

		String errorMsg = "";

		if (user.getName() == null || user.getName().isEmpty() || user.getName().length() < 4
				|| user.getName().length() > 50) {
			return errorMsg += "El nombre debe tener al menos 4 caracteres y no más de 50.";
		}

		if (user.getSurname() == null || user.getSurname().isEmpty() || user.getSurname().length() < 5
				|| user.getSurname().length() > 100) {
			return errorMsg += "Los apellidos deben tener al menos 5 caracteres y no más de 100.";
		}

		if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().length() < 3
				|| user.getEmail().length() > 90) {
			return errorMsg += "El email debe tener al menos 3 caracteres y no más de 90.";
		}

		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
		if (!user.getEmail().matches(emailRegex)) {
			return "El email no tiene un formato válido.";
		}

		if (updatePassword && (user.getEncodedPassword() == null || user.getEncodedPassword().isEmpty()
				|| user.getEncodedPassword().length() < 8 || user.getEncodedPassword().length() > 100)) {
			return errorMsg += "La contraseña debe tener al menos 8 caracteres y no más de 100.";
		}

		if (user.getAdress() != null && (user.getAdress().length() < 10 || user.getAdress().length() > 150)) {
			return errorMsg += "La dirección debe tener al menos 10 caracteres y no más de 150.";
		}
		return errorMsg;
	}

	public void updateUserFromEditedAndSave(User originalUser, User editedUser, boolean updatePassword,
			boolean updateImage, MultipartFile imageAvatar) {
		originalUser.setName(editedUser.getName());
		originalUser.setSurname(editedUser.getSurname());
		originalUser.setEmail(editedUser.getEmail());
		originalUser.setAdress(editedUser.getAdress());
		if (updatePassword) {
			originalUser.setEncodedPassword(passwordEncoder.encode(editedUser.getEncodedPassword()));
		}

		if (updateImage) {
			if (imageAvatar != null && !imageAvatar.isEmpty()) {
				try {
					save(originalUser, imageAvatar);
				} catch (IOException e) {
					throw new RuntimeException("Error al guardar la imagen", e);
				}
			} else {
				originalUser.setAvatar(null);
				save(originalUser);
			}
		} else {
			save(originalUser);
		}
	}

	public void setUserRoles(User user, String rol) {
		if (rol == null || !rol.equals("ADMIN")) {
            user.setRoles(List.of("USER"));
        } else {
            user.setRoles(List.of("USER", "ADMIN"));
        }
	}
}
