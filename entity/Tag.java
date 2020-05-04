package it.course.myblog.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tags")
@Data @AllArgsConstructor @NoArgsConstructor
public class Tag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NaturalId(mutable=true)
	@NotNull
	@Column(name = "tag_name", columnDefinition="VARCHAR(20)")
	private String tagName;
	
	public Tag(String tagName) {
		this.tagName = tagName;
	}

	@JsonBackReference
	@ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    private List<Post> posts = new ArrayList<>();
}
