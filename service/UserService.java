package it.course.myblog.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import it.course.myblog.security.UserPrincipal;

@Service
public class UserService {
	
	
	public static UserPrincipal getAuthenticatedUser() {
		
		// RECOVER FROM SECURITY CONTEXT THE USER LOGGED IN
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!authentication.getPrincipal().toString().equals("anonymousUser")) {
			return (UserPrincipal) authentication.getPrincipal();
		}
		return null;
		
	}

}
