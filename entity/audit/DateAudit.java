package it.course.myblog.entity.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
		value= {"createdAt","updatedAt"},
		allowGetters = false
		)
@Getter @Setter
public abstract class DateAudit implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@CreatedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "created_at", nullable=false, updatable=false)
	private Date createdAt;
	
	@LastModifiedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "updated_at", nullable=false)
	private Date updatedAt;

}
