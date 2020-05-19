package it.course.myblog.service;

import java.math.BigInteger;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import it.course.myblog.entity.LoginAttempts;
import it.course.myblog.entity.Users;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.LoginAttemptsRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.UserPrincipal;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	LoginAttemptsRepository loginAttemptsRepository;
	
	@Value("${app.login.time.to.unlock}")
	private int timeToUnlock;
	
	@Value("${app.login.max.attempt}")
	private int maxAttempts;
	
	
	public ResponseEntity<?> traceAttempts(Optional<Users> u, HttpServletRequest request) {
		String ip = PostService.findIp(request);
		Optional<LoginAttempts> la = Optional.of(new LoginAttempts());
		if(u.isPresent()){
			la = loginAttemptsRepository.findTop1ByUserIdOrderByUpdatedAtDesc(u.get().getId());
		}else {
			la= loginAttemptsRepository.findByIp(ip);
		}
		Instant dateLock = Instant.now().minus(timeToUnlock, ChronoUnit.SECONDS);
		
		if(la.isPresent() && la.get().getAttempts() < maxAttempts) {
			la.get().setAttempts(la.get().getAttempts()+1);
			la.get().setIp(ip);
			loginAttemptsRepository.save(la.get());
		} else if(la.isPresent() && la.get().getAttempts() == maxAttempts) {
			if(la.get().getUpdatedAt().isAfter(dateLock)) {
				la.get().setIp(ip);
				la.get().setUpdatedAt(Instant.now());
				loginAttemptsRepository.save(la.get());
				return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, "Unauthorized", "User locked", request.getRequestURI()), HttpStatus.FORBIDDEN);
			}else {
				la.get().setIp(ip);
				la.get().setAttempts(1);
				la.get().setUpdatedAt(Instant.now());
				loginAttemptsRepository.save(la.get());
			}
		}else {
			LoginAttempts l = new LoginAttempts(ip, 1, u.get().getId());
			loginAttemptsRepository.save(l);
		}	
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, "Unauthorized", "Bad credentials Service", request.getRequestURI()), HttpStatus.FORBIDDEN);
	}
	
	
	public static UserPrincipal getAuthenticatedUser() {
		
		// RECOVER FROM SECURITY CONTEXT THE USER LOGGED IN
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!authentication.getPrincipal().toString().equals("anonymousUser")) {
			return (UserPrincipal) authentication.getPrincipal();
		}
		return null;
		
	}
	
	public boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!authentication.getPrincipal().toString().equals("anonymousUser"))
			return true;
		return false;
	}

	public static byte[] getSHA(String input) throws NoSuchAlgorithmException 
    {  
        // Static getInstance method is called with hashing SHA  
        MessageDigest md = MessageDigest.getInstance("SHA-256");  
  
        // digest() method called  
        // to calculate message digest of an input  
        // and return array of byte 
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 
    
    public static String toHexString(byte[] hash) 
    { 
        // Convert byte array into signum representation  
        BigInteger number = new BigInteger(1, hash);  
  
        // Convert message digest into hex value  
        StringBuilder hexString = new StringBuilder(number.toString(16));  
  
        // Pad with leading zeros 
        while (hexString.length() < 32)  
        {  
            hexString.insert(0, '0');  
        }  
  
        return hexString.toString().toUpperCase();  
    } 
}
