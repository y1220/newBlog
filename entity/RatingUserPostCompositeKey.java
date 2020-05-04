package it.course.myblog.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor
public class RatingUserPostCompositeKey implements Serializable{
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post postId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users userId;
	
	private static final long serialVersionUID = 1L;

}
