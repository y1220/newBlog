package it.course.myblog.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Check;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rating")
@Check(constraints = "rating > 0 AND rating < 6")
@Data @AllArgsConstructor @NoArgsConstructor
public class Rating implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Column(name = "rating", columnDefinition= "TINYINT(1)")
	private int rating;
	
	@EmbeddedId
	private RatingUserPostCompositeKey ratingUserPostCompositeKey;
	
	public Rating(RatingUserPostCompositeKey ratingUserPostCompositeKey) {
		super();
		this.ratingUserPostCompositeKey = ratingUserPostCompositeKey;
	}
	
	@Transient
	private int countRate;

	public Rating(int rating, RatingUserPostCompositeKey ratingUserPostCompositeKey) {
		super();
		this.rating = rating;
		this.ratingUserPostCompositeKey = ratingUserPostCompositeKey;
	}

}
