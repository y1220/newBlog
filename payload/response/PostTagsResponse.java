package it.course.myblog.payload.response;

import java.util.Set;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostTagsResponse {
	
	private Long postId;
	private String title;
	private Set<Tag> tags;
	private double relevance;
	
	
	
	public static PostTagsResponse create(Post post) {
		return new PostTagsResponse(
			post.getId(),
			post.getTitle(),
			post.getTags(),
			Double.valueOf("0.0")
			);			
	}

	
}
