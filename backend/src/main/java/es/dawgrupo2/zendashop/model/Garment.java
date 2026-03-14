package es.dawgrupo2.zendashop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Garment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private final String reference = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    private String name;
    private BigDecimal price;
    private String category;
    private String description;
    private String features;
    private boolean available;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationDate;

    // TODO: Consider cascade relationship
    @OneToOne
    private Image image;

    @OneToMany(mappedBy = "garment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Opinion> opinions = new ArrayList<>();

    @OneToMany(mappedBy = "garment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Garment() {
    }

    public Garment(String name, BigDecimal price, String category, String description, String features) {
        super();
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.features = features;
        this.available = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(List<Opinion> opinions) {
        this.opinions = opinions;
    }

    public void addOpinion(Opinion opinion) {
        opinions.add(opinion);
        opinion.setGarment(this);
    }

    public void removeOpinion(Opinion opinion) {
        opinions.remove(opinion);
        opinion.setGarment(null);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getReference() {
        return reference;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setGarment(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setGarment(null);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}