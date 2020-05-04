package it.course.myblog.payload.request;

import javax.validation.constraints.NotNull;

//import lombok.NoArgsConstructor;

//@NoArgsConstructor
public class CommentRequest {

	@NotNull
	private String review;
	@NotNull
	private Long id;
	public CommentRequest(@NotNull String review, @NotNull Long id) {
		super();
		this.review = review;
		this.id = id;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	

	
}
