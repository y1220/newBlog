package it.course.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CountCommentGroupByPostResponse {
	
	private Long postId;
	private String postTitle;
	private Long countcomments;

}
