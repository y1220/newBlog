package it.course.myblog.payload.response;

import java.util.Set;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PostsByTags {
	
	private Long postId;
	private String postTitle;
	private String postContent;
	private Set<Tag> tags;
	private double relevance;
	
	public static PostsByTags create(Post post) {
		return new PostsByTags(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getTags(),
			Double.valueOf("0.0")
			);			
	}
	

}