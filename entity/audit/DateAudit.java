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
@JsonIgnoreProperties( //real time update
		value= {"createdAt","updatedAt"},
		allowGetters = false
		)
//@Getter @Setter -> not sure so don't use notification
public class DateAudit implements Serializable {

	@CreatedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "created_at", nullable=false, updatable=false)
	private Date createdAt;
	
	@LastModifiedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "updated_at", nullable=false)
	private Date updatedAt;

//	public DateAudit(Date createdAt, Date updatedAt) {
//		super();
//		this.createdAt = createdAt;
//		this.updatedAt = updatedAt;
//	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
}
