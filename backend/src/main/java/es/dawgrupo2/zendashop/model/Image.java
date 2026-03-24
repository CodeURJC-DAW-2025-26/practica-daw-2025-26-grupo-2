package es.dawgrupo2.zendashop.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private Blob imageFile;

    private boolean avatar;

    public Image() {
    }

    public Image(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public boolean getAvatar() {
        return avatar;
    }

    public void setAvatar(boolean avatar){
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Image [id=" + id + "]";
    }

}
