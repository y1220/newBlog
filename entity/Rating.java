package it.course.myblog.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Check;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rating")
@Check(constraints = "rating > 0 AND rating < 6")
@Data @AllArgsConstructor @NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "RatingUserPostCompositeKey")
@JsonIdentityReference(alwaysAsId = true)
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
	

}
