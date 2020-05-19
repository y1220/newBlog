package it.course.myblog.entity.audit;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
		value= {"createdBy","updatedBy"},
		allowGetters = true
		)
@Getter @Setter
public class UserAudit extends DateAudit{
	
	private static final long serialVersionUID = 1L;

	@CreatedBy
	private Long createdBy;
	
	@LastModifiedBy
	private Long updatedBy;

}
