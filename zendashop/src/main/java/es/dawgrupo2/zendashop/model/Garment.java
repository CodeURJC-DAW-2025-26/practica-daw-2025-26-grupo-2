package es.dawgrupo2.zendashop.model;

import java.math.BigDecimal;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Garment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	private BigDecimal price;
	private String category;
	private String description;
    private String features;

    @Column(updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Lob
	private Blob image;

    @OneToMany(mappedBy = "garment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Opinion> opinions = new ArrayList<>();

    @ManyToMany
    private List<Order> orders = new ArrayList<>();

	public Garment() {
	}

	public Garment(String name, BigDecimal price, String category, String description, String features) {
		super();
		this.name = name;
		this.price = price;
		this.category = category;
		this.description = description;
        this.features = features;
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

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}