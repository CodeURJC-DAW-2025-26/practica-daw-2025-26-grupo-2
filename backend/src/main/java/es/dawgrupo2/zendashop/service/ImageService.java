package es.dawgrupo2.zendashop.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import es.dawgrupo2.zendashop.model.Image;
import es.dawgrupo2.zendashop.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Image getImage(long id) {
        return imageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Imagen no encontrada"));
    }

    public Image createImage(InputStream inputStream) throws IOException {

        Image image = new Image();

        try {
            image.setImageFile(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        return image;
    }

    public Resource getImageFile(long id) throws SQLException {

        Image image = imageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Imagen no encontrada"));

        if (image.getImageFile() != null) {
            return new InputStreamResource(image.getImageFile().getBinaryStream());
        } else {
            throw new RuntimeException("Image file not found");
        }
    }

    public Image replaceImageFile(long id, InputStream inputStream) throws IOException {

        Image image = imageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Imagen no encontrada"));

        try {
            image.setImageFile(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);
        
        return image;
    }

    public Image deleteImage(long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Imagen no encontrada"));
        imageRepository.deleteById(id);
        return image;
    }

    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }
}