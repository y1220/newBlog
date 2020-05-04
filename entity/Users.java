package it.course.myblog.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OnDelete;

import it.course.myblog.entity.audit.DateAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints={@UniqueConstraint(columnNames={"username"})})
@Data @AllArgsConstructor @NoArgsConstructor
public class Users extends DateAudit {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(max = 20)
	private String username;
	
	@NaturalId(mutable=true) @NotNull @Email
	@Size(max = 120)
	private String email;
	
	@NotNull @Size(max = 100)
	private String password;
	
	@Size(max = 100)
	private String name;
	
	@Size(max = 100)
	private String lastname;
	
	@Column(name = "has_newsletter", nullable = false, columnDefinition = "tinyint(1) DEFAULT 0")
	private boolean hasNewsletter;
	
	@Column(name = "credit", nullable=false, columnDefinition = "INT(11) DEFAULT 0")
	private int credit;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();	
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="user_post_buyed", joinColumns = @JoinColumn(name="user_id"),
	inverseJoinColumns = @JoinColumn(name="post_id"))
	private Set<Post> posts = new HashSet<>();
	
	public Users(String username, String email, String password, String name, String lastname, boolean hasNewsletter) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.name = name;
		this.lastname = lastname;
		this.hasNewsletter = hasNewsletter;
	}
			
	

}