package it.course.myblog.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.course.myblog.entity.audit.ViewAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_viewed")
@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PostViewed extends ViewAudit{
	

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;
	
	private String ip;
	
	@Column(name="viewed_end")
	private Instant viewedEnd;
	

}
