package it.course.myblog.payload.response;

import java.util.List;

import it.course.myblog.entity.Comment;
import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CreditsByUser {
	
	Long userId;
	String username;
	int totalCredits;
	List<Post> posts;
	List<Comment> comments;

}
