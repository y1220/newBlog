package it.course.myblog.payload;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import it.course.myblog.entity.Users;
import it.course.myblog.security.UserPrincipal;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
import lombok.NoArgsConstructor;
//import lombok.Setter;

//@Getter @Setter @AllArgsConstructor 
@NoArgsConstructor
public class UserProfile {
	
	private Long id;
	private String username;
	private String name;
	private Date joinedAt;
	
	public UserProfile(Long id, String username, String name, Date joinedAt) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
		this.joinedAt = joinedAt;
	}
	
	// return all together
	public static UserProfile create(Users user) {
		return new UserProfile(
			user.getId(),
			user.getUsername(),
			user.getName(),
			user.getCreatedAt()		
			);			
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(Date joinedAt) {
		this.joinedAt = joinedAt;
	}

}
