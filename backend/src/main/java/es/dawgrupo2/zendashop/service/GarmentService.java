package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Image;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.repository.GarmentRepository;

@Service
public class GarmentService {

	@Autowired
	private GarmentRepository repository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OpinionService opinionService;

	@Autowired
	private ImageService imageService;

	public Optional<Garment> findById(long id) {
		return repository.findById(id);
	}

	public List<Garment> findById(List<Long> ids) {
		return repository.findAllById(ids);
	}

	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public Page<Garment> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public void save(Garment garment) {
		repository.save(garment);
	}

	public Garment create(Garment garment) {

		if (garment.getId() != null) {
			throw new IllegalArgumentException("El ID del prenda debe ser nulo para crear una nueva prenda");
		}

		garment.setAvailable(true);
		save(garment);

		return garment;
	}

    public Garment delete(long id) {
        Garment garment = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Prenda no encontrada"));
        repository.deleteById(id);
        return garment;
    }

	public Garment updateGarment(Garment originalGarment, Garment editedGarment) {
		originalGarment.setName(editedGarment.getName());
		originalGarment.setCategory(editedGarment.getCategory());
		originalGarment.setPrice(editedGarment.getPrice());
		originalGarment.setDescription(editedGarment.getDescription());
		originalGarment.setFeatures(editedGarment.getFeatures());
		repository.save(originalGarment);
		return originalGarment;
	}

	public Garment setImage(Garment garment, Image image) {
		garment.setImage(image);
		return repository.save(garment);
	}

	public long getCount() {
		return repository.count();
	}

	public Page<Garment> findByAvailableTrue(Pageable pageable) {
		return repository.findByAvailableTrue(pageable);
	}

	public Garment disable(Garment garment) {
		garment.setAvailable(false);
		// Delete opinions and remove them from users and garment to avoid orphan
		// records and maintain consistency
		for (Opinion opinion : new ArrayList<>(garment.getOpinions())) {
			User user = opinion.getUser();
			user.removeOpinion(opinion);
			garment.removeOpinion(opinion);
			opinionService.delete(opinion.getId());
		}
		// Disable garment in all carts to avoid problems with unavailable garments in
		// carts
		orderService.disableGarmentInCarts(garment);
		Long garmentId = garment.getImage().getId();
		garment.setImage(null);
		imageService.deleteImage(garmentId);
		save(garment);
		return garment;
	}

	public Page<Garment> findAvailableGarmentsByOptionalFilters(String name, String category, BigDecimal minPrice,
			BigDecimal maxPrice, Pageable pageable) {
		return repository.findAvailableGarmentsByOptionalFilters(name, category, minPrice, maxPrice, pageable);
	}

	public List<Garment> findSmartRecommendations(Long id) {
		return repository.findSmartRecommendations(id);
	}

	/*
	public String validateFields(Garment garment, MultipartFile imageFile, boolean updateImage) {

		String errorMsg = "";
		if (garment.getName() == null || garment.getName().isEmpty() || garment.getName().length() < 4
				|| garment.getName().length() > 100) {
			errorMsg += "El nombre no puede estar vacío, debe tener entre 4 y 100 caracteres. <br>";
		}
		if (garment.getPrice() == null || !(garment.getPrice() instanceof BigDecimal)
				|| garment.getPrice().compareTo(BigDecimal.valueOf(0)) <= 0
				|| garment.getPrice().compareTo(BigDecimal.valueOf(6000)) > 0) {
			errorMsg += "El precio debe ser un número positivo menor o igual a 6000. <br>";
		}
		if (garment.getCategory() == null || garment.getCategory().isEmpty()
				|| !garment.getCategory().matches("Camisas|Pantalones|Zapatos|Chaquetas|Accesorios|Otros")) {
			errorMsg += "La categoría tiene que ser una de las siguientes: Camisas, Pantalones, Zapatos, Chaquetas, Accesorios, Otros. <br>";
		}
		if (garment.getDescription() == null || garment.getDescription().isEmpty()
				|| garment.getDescription().length() < 3 || garment.getDescription().length() > 200) {
			errorMsg += "La descripción no puede estar vacía y debe tener entre 3 y 200 caracteres. <br>";
		}
		if (garment.getFeatures() == null || garment.getFeatures().isEmpty() || garment.getFeatures().length() < 3
				|| garment.getFeatures().length() > 300) {
			errorMsg += "Las características no pueden estar vacías y deben tener entre 3 y 300 caracteres. <br>";
		}
		if (updateImage && (imageFile == null || imageFile.isEmpty())) {
			errorMsg += "La imagen no puede estar vacía. <br>";
		} else if (updateImage && !imageFile.getContentType().startsWith("image")) {
			errorMsg += "El archivo debe ser una imagen de tipo JPG, JPEG o PNG. <br>";
		}

		return errorMsg;
	}
	*/

	public String validateFields(Garment garment) {

		String errorMsg = "";
		if (garment.getName() == null || garment.getName().isEmpty() || garment.getName().length() < 4
				|| garment.getName().length() > 100) {
			errorMsg += "El nombre no puede estar vacío, debe tener entre 4 y 100 caracteres. <br>";
		}
		if (garment.getPrice() == null || !(garment.getPrice() instanceof BigDecimal)
				|| garment.getPrice().compareTo(BigDecimal.valueOf(0)) <= 0
				|| garment.getPrice().compareTo(BigDecimal.valueOf(6000)) > 0) {
			errorMsg += "El precio debe ser un número positivo menor o igual a 6000. <br>";
		}
		if (garment.getCategory() == null || garment.getCategory().isEmpty()
				|| !garment.getCategory().matches("Camisas|Pantalones|Zapatos|Chaquetas|Accesorios|Otros")) {
			errorMsg += "La categoría tiene que ser una de las siguientes: Camisas, Pantalones, Zapatos, Chaquetas, Accesorios, Otros. <br>";
		}
		if (garment.getDescription() == null || garment.getDescription().isEmpty()
				|| garment.getDescription().length() < 3 || garment.getDescription().length() > 200) {
			errorMsg += "La descripción no puede estar vacía y debe tener entre 3 y 200 caracteres. <br>";
		}
		if (garment.getFeatures() == null || garment.getFeatures().isEmpty() || garment.getFeatures().length() < 3
				|| garment.getFeatures().length() > 300) {
			errorMsg += "Las características no pueden estar vacías y deben tener entre 3 y 300 caracteres. <br>";
		}

		return errorMsg;
	}

	public String validateImageField(MultipartFile imageFile, boolean updateImage){
		String errorMsg = "";
		if (updateImage && (imageFile == null || imageFile.isEmpty())) {
			errorMsg += "La imagen no puede estar vacía. <br>";
		} else if (updateImage && !imageFile.getContentType().startsWith("image")) {
			errorMsg += "El archivo debe ser una imagen de tipo JPG, JPEG o PNG. <br>";
		}
		return errorMsg;
	}

	public void updateImage(boolean updateImage, MultipartFile imageField, Garment garment) {
		if (updateImage) {
			try {
				Image image = imageService.replaceImageFile(garment.getImage().getId(), imageField.getInputStream());
				garment.setImage(image);
				repository.save(garment);
			} catch (IOException e) {
				throw new RuntimeException("Error al guardar la imagen", e);
			}
		} 
	}
}
