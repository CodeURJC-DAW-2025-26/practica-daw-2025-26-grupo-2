package es.dawgrupo2.zendashop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Opinion {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    @Transient
    private boolean isOwn;

    @Transient
    private String starsRating; // To store the star rating as a string of "★" characters for display purposes
    
    private Integer rating;
    private String comment;

    @ManyToOne
    private Garment garment;

    @ManyToOne
    private User user;

	public Opinion() {
	}

	public Opinion(Integer rating, String comment) {
		super();
		this.rating = rating;
        this.comment = comment;
	}

	public Long getId() {
		return id;
	}

    public void setId(Long id) {
		this.id = id;
	}

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Garment getGarment() {
        return garment;
    }

    public void setGarment(Garment garment) {
        this.garment = garment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return user.getName();
    }

    public boolean getOwn(){
        return isOwn;
    }

    public void setOwn(boolean isOwn){
        this.isOwn = isOwn;
    }

    public String getStarsRating() {
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }

    public Long getGarmentId(){
        return garment.getId();
    }    
}