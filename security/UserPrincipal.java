package it.course.myblog.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.course.myblog.entity.Users;

public class UserPrincipal implements UserDetails{
	

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String name;
	
	private String lastname;
	
	private String username;
	
	private String email;
	
	private String password;
	
	private Collection<? extends GrantedAuthority> authorities;
	

	public UserPrincipal(Long id, String name, String lastname, String username, String email, String password, 
			Collection<? extends GrantedAuthority> authorities) {
		super();
		this.id = id;
		this.name = name;
		this.lastname = lastname;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}


	public static UserPrincipal create(Users user) {
		
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());
		
		return new UserPrincipal(
			user.getId(),
			user.getName(),
			user.getLastname(),
			user.getUsername(),
			user.getEmail(),
			user.getPassword(),
			authorities				
			);			
	}


	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLastname() {
		return lastname;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(id);
    }
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		UserPrincipal that = (UserPrincipal) o;
		return Objects.equals(id, that.id);
		
	}
	
}
