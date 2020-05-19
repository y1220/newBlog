package it.course.myblog.payload.response;

import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.course.myblog.entity.Comment;
import it.course.myblog.entity.DBFile;
import it.course.myblog.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostResponseWithImg {
	
	private Long id;
	
	private String title;
	
	private Long updatedBy;
	
	@Temporal(TemporalType.DATE)
	private Date updatedAt;
	
	List<Comment> comments;
	
	private long visited;
	
	private DBFile dbFile;
	
	
	public static PostResponseWithImg create(Post post) {
		return new PostResponseWithImg(
			post.getId(),
			post.getTitle(),
			post.getUpdatedBy(),
			post.getUpdatedAt(),
			post.getComments(),
			Long.valueOf(0),
			post.getDbFile()
			);			
	}

}
