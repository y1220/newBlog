package it.course.myblog.controller;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Size;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.course.myblog.config.SecurityConfig;
import it.course.myblog.entity.Blacklist;
import it.course.myblog.entity.BlacklistReason;
import it.course.myblog.entity.Comment;
import it.course.myblog.entity.LoginAttempts;
import it.course.myblog.entity.Post;
import it.course.myblog.entity.Role;
import it.course.myblog.entity.RoleName;
import it.course.myblog.entity.Users;
import it.course.myblog.payload.request.BlacklistRequest;
import it.course.myblog.payload.request.LoginRequest;
import it.course.myblog.payload.request.SignUpRequest;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.payload.response.JwtAuthenticationResponse;
import it.course.myblog.repository.BlacklistReasonRepository;
import it.course.myblog.repository.BlacklistRepository;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.LoginAttemptsRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.RoleRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.JwtTokenProvider;
import it.course.myblog.security.UserPrincipal;
import it.course.myblog.service.CtrlUserBan;
import it.course.myblog.service.MailService;
import it.course.myblog.service.PostService;
import it.course.myblog.service.UserService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@Api(value="Users authorization management", description="All operations about users authorization")
public class AuthController {
		
	
			
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtTokenProvider tokenProvider;
	
	@Autowired
	BlacklistReasonRepository blacklistReasonRepository;
	
	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@Autowired
	BlacklistRepository blacklistRepository;
	
	@Autowired
	CtrlUserBan ctrlUserBan;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	LoginAttemptsRepository loginAttemptsRepository;
	
	@Autowired
	PostService postService;
	
	@Autowired
	UserService userService;
	
	@PostMapping("/signin")
	@ApiOperation(value="User login", response = ResponseEntity.class)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="User logged in"),
			@ApiResponse(code=401, message="Bad credentials or User Banned or Registration not confirmed"),
			@ApiResponse(code=404, message="User not found")
	})
	public ResponseEntity<?> authenticatUser(
			@ApiParam(value="LoginRequest Object", required=true) @Valid @RequestBody LoginRequest loginRequest,
			HttpServletRequest request){
		
		log.info("Call controller authenticatUser with parameter usernameOrEmail {}", loginRequest.getUsernameOrEmail() );
		Optional<Users> u = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(),loginRequest.getUsernameOrEmail());
		/*
		if(!u.isPresent()){
			log.error("User {} not found", loginRequest.getUsernameOrEmail());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, "Unauthorized", "Bad credentials_", request.getRequestURI()), HttpStatus.FORBIDDEN);
		}
		*/
		if(u.isPresent() && u.get().getRoles().size() < 1) {
			log.error("User {} not confirmed", loginRequest.getUsernameOrEmail());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, "Unauthorized", "User not confirmed. Please check your email.", request.getRequestURI()), HttpStatus.FORBIDDEN);
		}
		
		if(u.isPresent() && ctrlUserBan.isBanned(u.get()).isPresent()) {
			log.info("User {} unauthorized to log in. Reason: banned!", loginRequest.getUsernameOrEmail());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 401, "Unauthorized", "User Banned Until "+ctrlUserBan.isBanned(u.get()).get().getBlacklistedUntil(), request.getRequestURI()), HttpStatus.FORBIDDEN);
		}
		
		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					u.isPresent() ? u.get().getUsername() : " ", loginRequest.getPassword()		
				)
			);
		}catch(BadCredentialsException e) {
			return userService.traceAttempts(u, request);		
		}
			
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = tokenProvider.generateToken(authentication);
		log.info("User {} succesfully logged", loginRequest.getUsernameOrEmail());
		
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
		
	}
	
	
	@PutMapping("/signup-confirm/{identifier}")
	@ApiOperation(value="User registration", response = ResponseEntity.class)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="User confirmed with default role READER")
	})
	public ResponseEntity<ApiResponseCustom> signupConfirm(
			@ApiParam(value="identifier String", required=true) @PathVariable String identifier, HttpServletRequest request){
		
		log.info("Call controller signupConfirm with identifier: {}", identifier);
		
		Optional<Users> u = userRepository.findByIdentifierCode(identifier);
		
		if(!u.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 403, null, "User has just confirmed", request.getRequestURI()), HttpStatus.FORBIDDEN );
		}

		Role userRole = roleRepository.findByName(RoleName.ROLE_READER)
				.orElseThrow( () -> new RuntimeException());
		u.get().setRoles(Collections.singleton(userRole));
		u.get().setIdentifierCode(null);

		userRepository.save(u.get());
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "User confirmed", request.getRequestURI()), HttpStatus.OK );
		
	}

	@PostMapping("/signup")
	@Transactional
	@ApiOperation(value="User registration", response = ResponseEntity.class)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="User successfully registered with default role READER"),
			@ApiResponse(code=403, message="Username or email already in use")
	})
	public ResponseEntity<ApiResponseCustom> registerUser(@ApiParam(value="SignUpRequest Object", required=true) @Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
		
		log.info("Call controller registerUser with SignUpRequest as parameter: {}, {}, {}, {}", signUpRequest.getEmail(), signUpRequest.getUsername(), signUpRequest.getName(), signUpRequest.getLastname());
		
		if(userRepository.existsByUsername(signUpRequest.getUsername())) {
			log.info("Username {} already in use", signUpRequest.getUsername());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 403, null, "Username already in use !", request.getRequestURI()), HttpStatus.BAD_REQUEST);	
		}
		
		if(userRepository.existsByEmail(signUpRequest.getEmail())) {
			log.info("Email {} already in use", signUpRequest.getEmail());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 403, null, "Email already in use !", request.getRequestURI()), HttpStatus.BAD_REQUEST);
		}
		
		Users user = new Users(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword(), 
				signUpRequest.getName(), signUpRequest.getLastname(), signUpRequest.isHasNewsletter());
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		try {
			String identifier = UserService.toHexString(UserService.getSHA(Instant.now().toString()));
			user.setIdentifierCode(identifier);
			log.info("User has been update with identifier: {}", identifier);			
			String[] TO_ADDRESS = new String[]{user.getEmail()};
			try {
				mailService.send(TO_ADDRESS, "forgot", identifier);
			} catch (AddressException e) {
				log.error(e.getMessage());
			} catch (MessagingException e) {
				log.error(e.getMessage());
			}
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}		
		
		log.info("Email has been sent to {}", user.getEmail());
		
		userRepository.save(user);
		
		log.info("User creation successfully completed", signUpRequest.getEmail());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "User creation successfully completed", request.getRequestURI()), HttpStatus.OK );
		
	}
	
	@Transactional
	@PutMapping("/change-password/{identifier}/{newPassword}")
	@ApiOperation(value="Change password", response = ResponseEntity.class)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="Password has been modified"),
			@ApiResponse(code=401, message="Bad credentials")			
	})
	public ResponseEntity<ApiResponseCustom> changePassword(
			@ApiParam(value="identifier String", required=true) @PathVariable String identifier,
			@ApiParam(value="newPassword String", required=true) @PathVariable @Size(min=5, max=8) String newPassword, HttpServletRequest request){
		
		log.info("Call controller changePassword with parameter identifier {} and newPassword ******* ", identifier);
		
		Optional<Users> u = userRepository.findByIdentifierCode(identifier);
		if(!u.isPresent()) {
			log.error("User not found with identifier: {}", identifier);
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, "null", "User not found with identifier: "+identifier, request.getRequestURI()), HttpStatus.NOT_FOUND);
		}
		
		log.info("Encoding password");
		u.get().setPassword(passwordEncoder.encode(newPassword));
		u.get().setIdentifierCode(null);
		
		userRepository.save(u.get());
		
		log.info("Password has been modified by user {}", u.get().getUsername());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "Password has been modified by user "+u.get().getUsername(), request.getRequestURI()), HttpStatus.OK );
		
	}
	
	@PutMapping("/change-password-by-logged-user/{newPassword}")
	@PreAuthorize("hasRole('READER') or hasRole('EDITOR') or hasRole('MANAGING_EDITOR') or hasRole('ADMIN')")
	@ApiOperation(value = "Password modification : logged user", response = ResponseEntity.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Password has been modified")
	})
	public ResponseEntity<ApiResponseCustom> changePasswordByLoggedUser(
			@ApiParam(value = "New password of the user", required = true)
			@PathVariable
			@Size(min = 5, max = 20)
				String newPassword,
			HttpServletRequest request){
		
		UserPrincipal userPrincipal = UserService.getAuthenticatedUser();
		
		Users user = userRepository.findById(userPrincipal.getId()).get();
		
		log.info("Call controller changePasswordByLoggedUser with logged user {}", user.getUsername());
		
		log.info("Encoding password");
		user.setPassword(passwordEncoder.encode(newPassword));
		
		userRepository.save(user);
		
		log.info("User {} password changed succesfully", user.getUsername());
		
		return new ResponseEntity<ApiResponseCustom>(
				new ApiResponseCustom(Instant.now(), 200, null,	"Password has been modified succesfully!", request.getRequestURI()),
				HttpStatus.OK);
	}
	
	@PutMapping("/forgot-password/{usernameOrEmail}")
	@Transactional
	@ApiOperation(value="Change password", response = ResponseEntity.class)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="Email has been sent"),
			@ApiResponse(code=404, message="User not found")			
	})
	public ResponseEntity<ApiResponseCustom> forgotPassword(
			@ApiParam(value="usernameOrEmail String", required=true) @PathVariable @Size(min=3, max=120) String usernameOrEmail, HttpServletRequest request){
		
		log.info("Call controller forgotPassword with parameter usernameOrEmail: {}", usernameOrEmail);
		
		Optional<Users> u = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		if(!u.isPresent()) {
			log.error("User {} not found", usernameOrEmail);
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "User not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		}
		
		try {
			String identifier = UserService.toHexString(UserService.getSHA(Instant.now().toString()));
			u.get().setIdentifierCode(identifier);
			userRepository.save(u.get());
			log.info("User has been update with identifier: {}", identifier);			
			String[] TO_ADDRESS = new String[]{u.get().getEmail()};
			try {
				mailService.send(TO_ADDRESS, "forgot", identifier);
			} catch (AddressException e) {
				log.error(e.getMessage());
			} catch (MessagingException e) {
				log.error(e.getMessage());
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}		
		
		log.info("Email has been sent to {}", usernameOrEmail);
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "Email has been sent to: "+usernameOrEmail, request.getRequestURI()), HttpStatus.OK );
		
	}
	
	@PostMapping("/add-user-to-blacklist")
	@PreAuthorize("hasRole('READER')")
	@ApiOperation(value="Add user to blacklist", response = ResponseEntity.class)
	@ApiResponses(value= {
			@ApiResponse(code=200, message="User has been added to blacklist or Post/Comment has already been reported"),
			@ApiResponse(code=404, message="Blacklist Reason or Comment or Post not found")			
	})
	public ResponseEntity<ApiResponseCustom> addUserToBlacklist(
			@ApiParam(value="BlacklistRequest object", required=true) @Valid @RequestBody BlacklistRequest blacklistRequest, HttpServletRequest request){
		
		log.info("Call controller addUserToBlacklist with BlacklistRequest as parameter");
				
		Optional<Users> u = userRepository.findById(blacklistRequest.getUserId());
		if(!u.isPresent()) {
			log.error("User with id {} not found", blacklistRequest.getUserId());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "User not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		}
		
		// RECOVER FROM SECURITY CONTEXT THE USER LOGGED IN
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Optional<Users> reporter = userRepository.findById(userPrincipal.getId());
		
		Optional<BlacklistReason> blr = blacklistReasonRepository.findById(blacklistRequest.getBlacklistReasonId());
		if(!blr.isPresent()) {
			log.error("BlacklistReason with id {} not found", blacklistRequest.getBlacklistReasonId());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Blacklist Reason not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
			
		}
			
		Optional<Post> p = null;
		Comment c = new Comment();
		Long commentId = Long.valueOf(0);
		if(blacklistRequest.getCommentId() > Long.valueOf(0)) {
			
			c = commentRepository.findById(blacklistRequest.getCommentId()).get();
			if(c == null) {
				log.info("Comment not found");
				return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Comment not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
			}
			
			commentId = c.getId();
			p = Optional.of(c.getPost());
			
		} else {
			
			p = postRepository.findById(blacklistRequest.getPostId());
			if(!p.isPresent()) {
				log.info("Post with id {} not found", blacklistRequest.getPostId());
				return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 404, null, "Post not found", request.getRequestURI()), HttpStatus.NOT_FOUND);

			}
				
		}
		
		boolean isExist = blacklistRepository.existsByPostAndReporterAndCommentIdAndBlacklistReason(p.get(), reporter.get(), commentId, blr.get());
		
		if(!isExist) {
			Blacklist bl = new Blacklist(
					blacklistRequest.getBlacklistedFrom(),
					null,
					u.get(),
					p.get(),
					commentId,
					blr.get(),
					reporter.get(),
					false
					);
			
			blacklistRepository.save(bl);
			log.info("New Blacklist added");
		} else {
			log.info("Post/Comment has already been reported");
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "Post/Comment has already been reported", request.getRequestURI()), HttpStatus.OK );
		}		
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "User "+u.get().getUsername()+" has been added to blacklist", request.getRequestURI()), HttpStatus.OK );
		
	}
	
	
}
