package it.course.myblog.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor @Data @NoArgsConstructor
public class CommentRequest {
	
	@NotNull
	private String review;
	@NotNull
	private Long id;
	
}
