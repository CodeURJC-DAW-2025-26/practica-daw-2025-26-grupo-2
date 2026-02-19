package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;

import es.dawgrupo2.zendashop.model.Garment;
import es.dawgrupo2.zendashop.model.Opinion;
import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.User;

@Service
public class DatabaseInitializer {

	@Autowired
	private GarmentService garmentService;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {

		Garment garment1 = new Garment("Camiseta", BigDecimal.valueOf(19.99), "Ropa", "Camiseta de algodón",
				"Talla M, color blanco");

		Resource imageResource = new ClassPathResource("sample_images/camiseta.jpg");
		InputStream inputStream = imageResource.getInputStream();

		MockMultipartFile multipartFile = new MockMultipartFile(
				"image",
				"camiseta.jpg",
				"image/jpg",
				StreamUtils.copyToByteArray(inputStream));

		garmentService.save(garment1, multipartFile);
	}
}
