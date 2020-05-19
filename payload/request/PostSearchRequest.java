package it.course.myblog.payload.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostSearchRequest {
	
	@NotNull(message = "The keyword must not be null")
	@Size(min=3, message="The keyword is too short")
	private String keyword;
	
	private boolean caseSensitive;
	private boolean exactMatch;

}
