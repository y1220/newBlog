package it.course.myblog.payload.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostSearchResponse {
	
	private Long id;
	private String title;
	private Long createdBy;
	private Date createdAt;
	private Long relevance;
	@JsonIgnore
	private String content;
	
	public static PostSearchResponse create(Post post) {
		return new PostSearchResponse(
			post.getId(),
			post.getTitle(),
			post.getCreatedBy(),
			post.getCreatedAt(),
			Long.valueOf(0),
			post.getContent()
		);
	}

	
}
