package it.course.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorExcel {

	private String author;

	private Long totalPosts;
	private Long publishedPosts;
	private long postsToCheck;
	private long bannedPosts;

}
