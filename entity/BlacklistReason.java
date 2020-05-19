package it.course.myblog.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="blacklist_reason")
@Data @AllArgsConstructor @NoArgsConstructor
public class BlacklistReason {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="reason", nullable=false, columnDefinition="VARCHAR(120)")
	private String reason;
	
	@Column(name="days", nullable=false)
	private int days;

}
