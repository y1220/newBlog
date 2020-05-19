package it.course.myblog.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import it.course.myblog.entity.audit.LoginAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "login_attempts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginAttempts extends LoginAudit{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NaturalId @NotNull
	@Size(max = 40)
	private String ip;

	@NotNull
	@Column(columnDefinition = "TINYINT(1)")
	private int attempts;
	
	private Long userId;

	public LoginAttempts(String ip,int attempts,Long userId) {
		super();
		this.ip = ip;
		this.attempts = attempts;
		this.userId = userId;
	}
	
	
	
	
	

}
