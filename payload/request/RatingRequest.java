package it.course.myblog.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class RatingRequest {
	
	@NotNull
	private Long postId;
	@NotNull
	private int rating;

}
