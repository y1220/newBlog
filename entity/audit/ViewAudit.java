package it.course.myblog.entity.audit;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
		value= {"visitedBy","viewedStart"},
		allowGetters = true
		)
@Getter @Setter
public class ViewAudit implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	@CreatedBy
	private Long visitedBy;
	
	@CreatedDate
	private Instant viewedStart;
	
	

}
