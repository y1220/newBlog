package it.course.myblog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import it.course.myblog.entity.audit.UserAudit;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
//getter setter constructor
@NoArgsConstructor
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class Comment extends UserAudit {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "review", nullable = false, columnDefinition = "TEXT")
	private String review;
	
	@Column(name = "is_visible", nullable=false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isVisible;
	
	@JsonBackReference // Resolve infinite Recursion with Jackson JSON and Hibernate JPA in bidirectional association
	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;
	
	@JsonManagedReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="credit_id")
	private Credit credit;
	
	

	public Comment(Long id, String review, boolean isVisible, Post post) {
		super();
		this.id = id;
		this.review = review;
		this.isVisible = isVisible;
		this.post = post;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Credit getCredit() {
		return credit;
	}

	public void setCredit(Credit credit) {
		this.credit = credit;
	}

	public Comment(Long id, String review, boolean isVisible, Post post, Credit credit) {
		super();
		this.id = id;
		this.review = review;
		this.isVisible = isVisible;
		this.post = post;
		this.credit = credit;
	}

	
}
