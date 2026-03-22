package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.repository.UserRepository;
import es.dawgrupo2.zendashop.model.Image;
import es.dawgrupo2.zendashop.model.User;

@Service
public class UserService {

	private final ImageService imageService;

	@Autowired
	private UserRepository repository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	UserService(ImageService imageService) {
		this.imageService = imageService;
	}

	public Optional<User> findById(long id) {
		return repository.findById(id);
	}

	public Optional<User> findByIdAndDisabledFalse(long id) {
		return repository.findByIdAndDisabledFalse(id);
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

	public User createUser(User user) {

		if (user.getId() != null) {
			throw new IllegalArgumentException("El ID del usuario debe ser nulo para crear un nuevo usuario");
		}

		save(user);

		return user;
	}

	public User delete(long id) {
		User user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		repository.deleteById(id);
		return user;
	}

	public Optional<User> findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public User disableUser(long id) {
		//User originalUser = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		User user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		User originalUser = new User();
		BeanUtils.copyProperties(user, originalUser); //this is for returning the original user before the disabling
		if (originalUser.getAvatar() != null) {
			long imageId = user.getAvatar().getId();
			user.setAvatar(null);
			imageService.deleteImage(imageId);
		}
		if (user != null) {
			user.setDisabled(true);
			if (user.getCart() != null) {
				orderService.deleteCart(user.getCart());
				this.save(user);
			}
			if (user.getOrders().size() > 0) {
				String uuid = UUID.randomUUID().toString();
				user.setName("Anonymous" + uuid);
				user.setSurname("Anonymous" + uuid);
				user.setEmail(uuid + "@anonymous.com");
				user.setEncodedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
				orderService.deleteCart(user.getCart());
				this.save(user);
			} else {
				this.delete(id);
			}
		}
		return originalUser;
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

		if (user.getAddress() != null && (user.getAddress().length() < 10 || user.getAddress().length() > 150)) {
			return errorMsg += "La dirección debe tener al menos 10 caracteres y no más de 150.";
		}
		return errorMsg;
	}

	// Handles all the logic related to updating the user's fields, including password encoding if necessary
	public User updateUser(User originalUser, User editedUser, boolean updatePassword,
			boolean updateImage) {
		originalUser.setName(editedUser.getName());
		originalUser.setSurname(editedUser.getSurname());
		originalUser.setEmail(editedUser.getEmail());
		originalUser.setAddress(editedUser.getAddress());
		if (updatePassword) {
			originalUser.setEncodedPassword(passwordEncoder.encode(editedUser.getEncodedPassword()));
		}
		return originalUser;
	}

	public void setUserRoles(User user, String rol) {
		if (rol == null || !rol.equals("ADMIN")) {
			user.setRoles(List.of("USER"));
		} else {
			user.setRoles(List.of("USER", "ADMIN"));
		}
	}

	// Handles all the logic related to updating the user's avatar, including deleting the old one if necessary
	public void updateAvatar(User user, MultipartFile imageField, boolean updateImage) {
		if (updateImage) {
			if (imageField != null && !imageField.isEmpty()) {
				if (user.getAvatar() != null) {
					try {
						Image avatar = imageService.replaceImageFile(user.getAvatar().getId(),
								imageField.getInputStream());
						user.setAvatar(avatar);
					} catch (IOException e) {
						throw new RuntimeException("Error al guardar la imagen", e);
					}
				} else {
					try {
						Image avatar = imageService.createImage(imageField.getInputStream());
						user.setAvatar(avatar);
					} catch (IOException e) {
						throw new RuntimeException("Error al guardar la imagen", e);
					}
				}
			} else {
				if (user.getAvatar() != null) {
					Long imageId = user.getAvatar().getId();
					user.setAvatar(null);
					imageService.deleteImage(imageId);
				}
			}
		}
	}

	public void setEncodedPassword(User user) {
		user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
	}

	public void setImage(User user, Image image) {
		user.setAvatar(image);
		imageService.saveImage(image);
		this.save(user);
	}
}
