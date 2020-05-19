package it.course.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class CountPosts {
	
	private String username;
	private Long count;

}
