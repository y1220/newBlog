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
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import it.course.myblog.entity.audit.UserAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comment")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIdentityReference(alwaysAsId = true)
public class Comment extends UserAudit{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "review", nullable=false, columnDefinition= "TEXT")
	private String review;
	
	@Column(name = "is_visible", nullable=false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isVisible;
	
	//@JsonBackReference(value="comment-post") // Resolve infinite Recursion with Jackson JSON and Hibernate JPA in bidirectional association
	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;
	
	//@JsonManagedReference(value="credit-comment")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="credit_id")
	private Credit credit;

}
