package it.course.myblog.payload;

import java.util.Date;

import it.course.myblog.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserProfile {
	
	private Long id;
	private String username;
	private String name;
	private Date joinedAt;
	
	
	public static UserProfile create(Users user) {
		return new UserProfile(
			user.getId(),
			user.getUsername(),
			user.getName(),
			user.getCreatedAt()		
			);			
	}

}
