package es.dawgrupo2.zendashop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Garment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	private Integer price;
	private Integer finalPrice;
	private Integer discount;
	private boolean offer;
	private String category;
	private String description;
    private String features;
	private String image;

	public Garment() {
	}

	public Garment(String name, Integer price, Integer discount, boolean offer, String category, String description, String features, String image) {
		super();
		this.name = name;
		this.price = price;
		this.finalPrice = price*((100-discount)/100);
		this.discount = discount;
		this.offer = offer;
		this.category = category;
		this.description = description;
        this.features = features;
		this.image = image;
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

	public Integer getPrice() {
        return price;
    }

    public Integer getFinalPrice() {
        return finalPrice;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public boolean getOffer() {
		return offer;
	}

	public void setOffer(boolean offer) {
		this.offer = offer;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}