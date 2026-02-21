package es.dawgrupo2.zendashop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity(name = "OrderTable")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    private boolean completed;
    private String deliveryAddress;
    private LocalDate deliveryDate;
    private String deliveryNote;
    private BigDecimal shippingCost;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationDate;
    
    @ManyToOne
    private User user;

    @ManyToMany(mappedBy = "orders")
    private List<Garment> garments = new ArrayList<>();

    public Order() {
	}

    public Order(boolean completed, String deliveryAddress, LocalDate deliveryDate, String deliveryNote, BigDecimal shippingCost) {
        this.completed = completed;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDate = deliveryDate;
        this.deliveryNote = deliveryNote;
        this.shippingCost = shippingCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(String deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public List<Garment> getGarments() {
		return garments;
	}

	public void setGarments(List<Garment> garments) {
		this.garments = garments;
	}

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void addGarment(Garment garment) {
        garment.addOrder(this);
        this.garments.add(garment);
    }

    public void removeGarment(Garment garment) {
        this.garments.remove(garment);
    }
}
