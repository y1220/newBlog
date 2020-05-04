package it.course.myblog.entity;

import java.time.LocalDate;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blacklist")
@Data @AllArgsConstructor @NoArgsConstructor
public class Blacklist {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "blacklisted_from", nullable=false)
	private LocalDate blacklistedFrom;
	
	@Column(name = "blacklisted_until", nullable=true)
	private LocalDate blacklistedUntil;
	
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users user;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;
	
	@Column(name = "comment_id", nullable=true)
	private Long commentId;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "blacklistReason_id")
	private BlacklistReason blacklistReason;
	
	@ManyToOne
	@JoinColumn(name="reported_by")
	private Users reporter;
	
	@Column(name = "is_verified", nullable=false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isVerified;

	public Blacklist(LocalDate blacklistedFrom, LocalDate blacklistedUntil, Users user, Post post, Long commentId,
			BlacklistReason blacklistReason, Users reporter, boolean isVerified) {
		super();
		this.blacklistedFrom = blacklistedFrom;
		this.blacklistedUntil = blacklistedUntil;
		this.user = user;
		this.post = post;
		this.commentId = commentId;
		this.blacklistReason = blacklistReason;
		this.reporter = reporter;
		this.isVerified = isVerified;
	}
	
	
	
	

}
