package es.dawgrupo2.zendashop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity(name = "OrderTable")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean completed;
    private String deliveryAddress;
    private LocalDate deliveryDate;
    private String deliveryNote;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationDate;

    @ManyToOne
    private User user;

    @OneToOne(mappedBy = "cart")
    private User cartUser;

    @ManyToMany(mappedBy = "orders")
    private List<Garment> garments = new ArrayList<>();

    public Order() {
    }

    public Order(boolean completed, String deliveryAddress, LocalDate deliveryDate, String deliveryNote) {
        this.completed = completed;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDate = deliveryDate;
        this.deliveryNote = deliveryNote;
        this.shippingCost = BigDecimal.valueOf(0);
        this.subtotal = BigDecimal.valueOf(0);
        this.totalPrice = subtotal.add(shippingCost);
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

    public void calculateShippingCost() {
        if (subtotal.compareTo(BigDecimal.valueOf(50)) == -1) {
            setShippingCost(BigDecimal.valueOf(4.99));
        } else {
            setShippingCost(BigDecimal.valueOf(0));
        }
    }

    public void calculateTotal() {
        this.totalPrice = this.subtotal.add(this.shippingCost);
    }

    public void addGarment(Garment garment, int quantity) {
        for (int i = 1; i <= quantity; i++) {
            this.garments.add(garment);
        }
        garment.addOrder(this);
        this.subtotal = this.subtotal.add(garment.getPrice().multiply(BigDecimal.valueOf(quantity)));
        calculateShippingCost();
        calculateTotal();
    }

    public void removeGarment(Garment garment) {
        if (garments.stream().anyMatch(g -> g.getId() == garment.getId())) {
            int previousSize = garments.size();
            garments.removeIf(g -> g.getId() == garment.getId());
            int removed = previousSize - garments.size();
            garment.removeOrder(this);
            this.subtotal = this.subtotal.subtract(garment.getPrice().multiply(BigDecimal.valueOf(removed)));
            calculateShippingCost();
        }
    }

    public List<Garment> setQuantities() {
    if (this.garments == null || this.garments.isEmpty()) {
        return new ArrayList<>();
    }

    // 1. Contar con total precisión usando IDs
    Map<Long, Integer> counts = new HashMap<>();
    for (Garment g : this.garments) {
        if (g.getId() != null) {
            counts.put(g.getId(), counts.getOrDefault(g.getId(), 0) + 1);
        }
    }

    // 2. Usar un Map para quedarnos con un solo objeto por ID
    Map<Long, Garment> distinctMap = new LinkedHashMap<>();
    for (Garment g : this.garments) {
        if (!distinctMap.containsKey(g.getId())) {
            // Seteamos la cantidad obtenida del mapa de conteo
            int total = counts.getOrDefault(g.getId(), 0);
            g.setQuantity(total); 
            
            distinctMap.put(g.getId(), g);
        }
    }

    // 3. Verificación inmediata en consola
    return(new ArrayList<>(distinctMap.values()));
}
}
