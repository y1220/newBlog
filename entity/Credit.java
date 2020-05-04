package it.course.myblog.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credit")
@Data @AllArgsConstructor @NoArgsConstructor
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
	
	@JsonBackReference
	@OneToMany(mappedBy = "credit")
	private List<Post> posts = new ArrayList<Post>();
	
	@JsonBackReference
	@OneToMany(mappedBy = "credit")
	private List<Comment> comments = new ArrayList<Comment>();
	

}
