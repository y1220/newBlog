package it.course.myblog.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.course.myblog.entity.audit.DateAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints={@UniqueConstraint(columnNames={"username"})})
//@Data 
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode(callSuper=false)
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
	
	@JsonManagedReference
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="user_post_buyed", joinColumns = @JoinColumn(name="user_id"),
	inverseJoinColumns = @JoinColumn(name="post_id"))
	private Set<Post> posts = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_preferred_tags", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> preferredTags = new HashSet<>();

	@Size(max = 64)
	private String identifierCode;
	
	public Users(String username, String email, String password, String name, String lastname, boolean hasNewsletter) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.name = name;
		this.lastname = lastname;
		this.hasNewsletter = hasNewsletter;
	}
	
	public Users(Long id, String username, String email, String identifierCode) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.identifierCode = identifierCode;
	}

	public Users(Long id, String username, String email,
			String password, String name, String lastname,
			boolean hasNewsletter, Set<Role> roles) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.name = name;
		this.lastname = lastname;
		this.hasNewsletter = hasNewsletter;
		this.roles = roles;
	}

	

}
