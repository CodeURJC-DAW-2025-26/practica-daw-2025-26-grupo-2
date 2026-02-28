package es.dawgrupo2.zendashop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

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

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
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
        totalPrice = subtotal.add(shippingCost);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
        subtotal = subtotal.add(orderItem.getSubtotal());
        calculateShippingCost();
        calculateTotal();
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        subtotal = subtotal.subtract(orderItem.getSubtotal());
        calculateShippingCost();
        calculateTotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void updateTotalPrice(){
        subtotal = BigDecimal.valueOf(0);
        for (OrderItem item : orderItems) {
            subtotal = subtotal.add(item.getSubtotal());
        }
        calculateShippingCost();
        calculateTotal();
    }

    public String getConvertCreationDateToLocalDate() {
        return creationDate.toLocalDate().toString();
    }
}