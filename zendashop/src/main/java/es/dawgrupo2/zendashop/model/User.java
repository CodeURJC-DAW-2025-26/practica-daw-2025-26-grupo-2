package es.dawgrupo2.zendashop.model;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity(name = "UserTable")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
    private String surname;
    private String email;
    private String adress;
    private LocalDateTime creationDate;
	private String encodedPassword;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    @Lob
    private Blob avatar;

    @OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<Opinion> opinions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

	public User() {
	}

	public User(String name, String surname, String email, String adress, String encodedPassword, String... roles) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.adress = adress;
		this.encodedPassword = encodedPassword;
		this.roles = List.of(roles);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Blob getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        this.avatar = avatar;
    }

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

    public List<Opinion> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<Opinion> opinions) {
		this.opinions = opinions;
	}

	public void addOpinion(Opinion opinion) {
        opinions.add(opinion);
        opinion.setUser(this);
    }
 
    public void removeOpinion(Opinion opinion) {
        opinions.remove(opinion);
        opinion.setUser(null);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }
 
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }
}