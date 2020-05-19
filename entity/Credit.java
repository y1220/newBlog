package it.course.myblog.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credit")
@Data @AllArgsConstructor @NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIdentityReference(alwaysAsId = true)
public class Credit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "credit_description", nullable=false, columnDefinition= "VARCHAR(20)")
	private String creditDescription;
	
	@NaturalId
	@Column(name = "credit_code", nullable=false, columnDefinition= "VARCHAR(3)")
	private String creditCode;
	
	@Column(name = "credit_import", nullable=false)
	private int creditImport;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "start_Date", nullable=false)
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "end_Date", nullable=true)
	private Date endDate;
	
	

	public Credit(String creditDescription, String creditCode,  int creditImport, Date startDate, Date endDate) {
		super();
		this.creditDescription = creditDescription;
		this.creditCode = creditCode;
		this.creditImport = creditImport;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	//@JsonBackReference(value="credit-post")
	@OneToMany(mappedBy = "credit", cascade = CascadeType.ALL)
	private List<Post> posts = new ArrayList<Post>();
	
	//@JsonBackReference(value="credit-comment")
	@OneToMany(mappedBy = "credit", cascade = CascadeType.ALL)
	private List<Comment> comments = new ArrayList<Comment>();
	

}
