package it.course.myblog.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.course.myblog.entity.Users;
import it.course.myblog.exception.ResourceNotFoundException;
import it.course.myblog.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) {
		
		Users user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow( 
						() -> new UsernameNotFoundException("User not found with username or email: " +usernameOrEmail)
						);
	
		return UserPrincipal.create(user);
	}
	
	
	public UserDetails loadUserById(Long id) {
		
		Users user = userRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("User", "id", id)
				);
		return UserPrincipal.create(user);
	}

}
