package it.course.myblog.payload.response;

import java.time.Instant;

import it.course.myblog.entity.PostViewed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostViewedResponse {
	
	private Long id;
	private Long postId;
	private String postTitle;
	
	private Instant viewedStart;
	
	private Instant viewedEnd;
	private String ip;
	private String username;
	
	public static PostViewedResponse create(PostViewed postViewed) {
		
		return new PostViewedResponse(
			postViewed.getId(),
			postViewed.getPost().getId(),
			postViewed.getPost().getTitle(),
			postViewed.getViewedStart(),
			postViewed.getViewedEnd(),
			postViewed.getIp(),
			"anonymous"
			);		
	}

}
