package it.course.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ReaderExcel {
	
	private String username;
	
	private Long totalComments;
	private Long publishedComments;
	private Long commentsToCheck;
	private Long bannedComments;
	private Long boughtPosts;
	
	private Long credits;
}
