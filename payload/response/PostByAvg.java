package it.course.myblog.payload.response;

import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PostByAvg {
 
	private Long postId;
	private String title;
	private Double avgRating;
 
	public static PostByAvg create(Post post) {
		return new PostByAvg(
		   post.getId(),
		   post.getTitle(),
		   post.getAvgRating()
		   );   
	}

}
