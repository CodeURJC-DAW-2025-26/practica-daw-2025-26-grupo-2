package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

	public List<User> findById(List<Long> ids){
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

	public void save(User user, MultipartFile imageFile) throws IOException{
        if(!imageFile.isEmpty()) {
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
			}
			else{
				if (user.getCart() != null) {
					orderService.deleteCart(user.getCart());
				}
				this.delete(id);
			}
			
		}
	}

	public List<User> findByDisabledFalse(Pageable pageable) {
		return repository.findByDisabledFalse(pageable);
	}
}
