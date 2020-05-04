package it.course.myblog.entity.audit;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties( //real time update
		value= {"createdBy","updatedBy"},
		allowGetters = true
		)
// getter setter
public class UserAudit extends DateAudit {

	@CreatedBy
	private Long createdBy;
	
	@LastModifiedBy
	private Long updatedBy;

//	public UserAudit(Long createdBy, Long updatedBy) {
//		super();
//		this.createdBy = createdBy;
//		this.updatedBy = updatedBy;
//	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
	
}
