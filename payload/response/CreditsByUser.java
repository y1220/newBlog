package it.course.myblog.payload.response;

import java.util.List;

import it.course.myblog.entity.Comment;
import it.course.myblog.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class CreditsByUser {

	
	Long userId;
	String username;
	int totalCredits;
	List<Post> posts;
	List<Comment> comments;
	
	public CreditsByUser(Long userId, String username, int totalCredits, List<Post> posts, List<Comment> comments) {
		super();
		this.userId = userId;
		this.username = username;
		this.totalCredits = totalCredits;
		this.posts = posts;
		this.comments = comments;
	}
	
}
