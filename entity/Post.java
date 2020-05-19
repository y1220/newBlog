package it.course.myblog.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import it.course.myblog.entity.audit.UserAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "post")
/*@Data */ @Getter @Setter @AllArgsConstructor @NoArgsConstructor
//@EqualsAndHashCode(callSuper=false)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIdentityReference(alwaysAsId = true)
public class Post extends UserAudit{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "title", nullable=false, columnDefinition = "VARCHAR(100)")
	private String title;
	
	@Column(name = "content", nullable=false, columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "is_visible", nullable=false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isVisible;
	
	@Column(name = "is_approved", nullable=false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isApproved;
	
	//@JsonManagedReference(value="comment-post") // Resolve infinite Recursion with Jackson JSON and Hibernate JPA in bidirectional association
	@OneToMany(mappedBy="post", fetch= FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval=true)
	private List<Comment> comments = new ArrayList<Comment>();
	
	//@JsonManagedReference(value="tag-post")
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="post_tags", joinColumns = @JoinColumn(name="post_id"),
	inverseJoinColumns = @JoinColumn(name="tag_id"))
	@OnDelete(action=OnDeleteAction.CASCADE) 
	private Set<Tag> tags = new HashSet<>();
	
	//@JsonManagedReference(value="blacklist-post")
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy="post", cascade=CascadeType.MERGE, orphanRemoval=true)
	private List<Blacklist> blacklists;
	
	//@JsonManagedReference(value="credit-post")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="credit_id")
	private Credit credit;
	
	//@JsonManagedReference(value="users-who-bought-posts")
	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "posts")
	private Set<Users> usersWhoBought = new HashSet<>();
	
	@Column(name = "avg_rating", columnDefinition="DECIMAL(3,2)")
	private Double avgRating;
	
	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dbFile_id")
    private DBFile dbFile;

	public Post(Long id, String title, String content, boolean isVisible, boolean isApproved, DBFile dbFile) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.isVisible = isVisible;
		this.isApproved = isApproved;
		this.dbFile = dbFile;
	}
	
	public Post(String title, String content, boolean isVisible, boolean isApproved, DBFile dbFile) {
		super();
		this.title = title;
		this.content = content;
		this.isVisible = isVisible;
		this.isApproved = isApproved;
		this.dbFile = dbFile;
	}
	
	
}
