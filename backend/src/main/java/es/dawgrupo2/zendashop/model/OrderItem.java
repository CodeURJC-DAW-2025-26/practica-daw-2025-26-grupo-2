package es.dawgrupo2.zendashop.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int quantity;

    private String size;

    @ManyToOne
    private Garment garment;

    @ManyToOne
    private Order order;

    public OrderItem() {
    }

    public OrderItem(int quantity, String size, Garment garment) {
        this.quantity = quantity;
        this.size = size;
        this.garment = garment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Garment getGarment() {
        return garment;
    }

    public void setGarment(Garment garment) {
        this.garment = garment;
    }

    public BigDecimal getSubtotal() {
        return garment.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getGarmentName() {
        return garment.getName();
    }

    public String getGarmentReference() {
        return garment.getReference();
    }
    
    public BigDecimal getGarmentPrice() {
        return garment.getPrice();
    }

    public long getGarmentId() {
        return garment.getId();
    }
}
